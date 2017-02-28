/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases;

import com.hp.snap.evaluation.imdb.business.common.CallService;

/**
 * @author Yang, Lin
 */
public class C6Async {
    private boolean _background;

    public static void main(String[] args) throws Exception {
        C6Async app = new C6Async();
        app.configure(args);
        try {
            app.start();
        } finally {
            app.exit();
        }
    }

    private CallService _service;

    private void start() throws Exception {
        _service.console(_background);
    }

    private void exit() throws Exception {
        CallImpl6JAsync.shutdown();
    }

    private void configure(String[] args) throws Exception {
        for (String arg : args) {
            if ("-background".equalsIgnoreCase(arg)) {
                System.out.println("*** Working in background mode !");
                _background = true;
            }
        }

        _service = new CallService(getClass().getSimpleName());
        _service.configure();
        CallImpl6JAsync.initialize();
    }
}
