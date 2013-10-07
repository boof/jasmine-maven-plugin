package jasmine.plugin;

import jasmine.plugin.readers.DirectoryReader;
import jasmine.plugin.readers.HtmlReader;
import jasmine.plugin.readers.SuiteReader;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.shell.Global;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class JasmineRunner {

    static public String ENV_RHINO_JS = "/env.rhino.1.2.js";
    static public String ENV_TIMER_JS = "/env.timer.js";
    static public String JASMINE_JS   = "/jasmine.1.3.1.js";
    static public String RUNNER_JS    = "/jasmine-runner.js";
    static public String REPORTER_JS  = "/jasmine-surefire-reporter.js";

    private final File baseDirectory;
    private final Log log;

    public JasmineRunner(File directory, Log log) {
        this.baseDirectory = directory;
        this.log = log;
    }

    public void run()
            throws MojoExecutionException {
        Iterable<String> suite = getSuiteReader().getSuite();

        Context ctx = getContext();
        Global scope = new Global(ctx);
        Object report;

        final TimerManager timers = new TimerManager(ctx, scope);

        try {
            loadResource(ENV_RHINO_JS, ctx, scope);

            scope.defineProperty("setNativeTimeout", new BaseFunction() {
                public Object call(Context ctx, Scriptable scope, Scriptable thisObj, Object[] args) {
                    return timers.setTimeout((Function) args[0], ((Double) args[1]).intValue());
                }

                public int getArity() {
                    return 2;
                }
            }, ScriptableObject.DONTENUM);

            loadResource(ENV_TIMER_JS, ctx, scope);
            loadResource(JASMINE_JS, ctx, scope);

            for (String spec : suite) {
                loadSpec(spec, ctx, scope);
            }

            loadResource(REPORTER_JS, ctx, scope);
            loadResource(RUNNER_JS, ctx, scope);

            timers.waitForTimers(2000);

            report = scope.get("report", scope);

            // TODO write report into file
            log.debug((String) report);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Context.exit();
        }
    }

    private Context getContext() {
        Context ctx = Context.enter();
        ctx.setOptimizationLevel(-1);
        ctx.setLanguageVersion(Context.VERSION_1_5);
        return ctx;
    }

    private SuiteReader getSuiteReader() {
        // TODO how to properly implement a Builder here
        boolean nothingToRead = !baseDirectory.exists();
        if (nothingToRead) {
            return new SuiteReader();
        } else {
            File runnerHtml = new File(baseDirectory, "SpecRunner.html");
            boolean hasRunnerHtml = runnerHtml.exists();

            if (hasRunnerHtml) {
                return new HtmlReader(runnerHtml);
            } else {
                return new DirectoryReader(baseDirectory);
            }
        }

    }

    private void loadResource(String name, Context ctx, Scriptable scope) throws IOException {
        log.info("Processing " + name);

        InputStream stream = JasmineMojo.class.getResourceAsStream(name);
        InputStreamReader in = new InputStreamReader(stream);
        ctx.evaluateReader(scope, in, getBasename(name), 0, null);
    }

    private void loadSpec(String path, Context ctx, Scriptable scope) throws IOException {
        log.info("Processing " + path);
        FileReader in = new FileReader(path);
        ctx.evaluateReader(scope, in, getBasename(path), 0, null);
    }

    private String getBasename(String path) {
        return FilenameUtils.getBaseName(path);
    }

    class TimerManager extends ScriptableObject {
        private final Context ctx;
        private final Global scope;
        private final Timer timer = new Timer();

        private List<TimerTask> tasks = new ArrayList<TimerTask>();

        public TimerManager(Context ctx, Global scope) {
            this.ctx = ctx;
            this.scope = scope;
        }

        final AtomicInteger taskCounter = new AtomicInteger(0);

        public Integer setTimeout(final Function fn, Integer delay) {
            taskCounter.incrementAndGet();

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        fn.call(ctx, scope, scope, new Object[0]);
                    } finally {
                        taskCounter.decrementAndGet();
                    }
                }
            };

            int newIndex = tasks.size();
            tasks.add(task);
            timer.schedule(task, delay);

            return newIndex;
        }

        public Integer setInterval(final Function fn, Integer interval) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    fn.call(ctx, scope, scope, null);
                }
            };

            int newIndex = tasks.size();
            tasks.add(task);
            timer.schedule(task, interval, interval);

            return newIndex;
        }
        public void clearTimer(Integer index) {
            tasks.get(index).cancel();
        }

        public final long TIME_TO_SLEEP = 10;

        public void waitForTimers(long timeout) {
            long iterations = timeout / TIME_TO_SLEEP;
            while (taskCounter.get() > 0 && iterations > 0) {
                try {
                    Thread.sleep(TIME_TO_SLEEP);
                    iterations--;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            for (TimerTask task : tasks) {
                task.cancel();
            }
        }

        @Override
        public String getClassName() {
            return "[Object TimerManager]";
        }
    }


}