package com.hp.snap.evaluation.imdb.business.cases;

import com.hp.snap.evaluation.imdb.business.common.CallService;

public class C4JPerf {
    private boolean _background;

    public static void main(String[] args) throws Exception {
        C4JPerf app = new C4JPerf();
        app.configure(args);
        try {
            app.start();
        } catch (Throwable e) {
        	e.printStackTrace();
        } finally {
            app.exit();
        }
    }

    private CallService _service;

    private void start() throws Exception {
        _service.console(_background);
    }

//    private void exit() throws Exception {
//        validate();
//    }
    private void exit() throws Exception {
    	CallImpl4JPerf.shutdown();
    }

//    @Override
    public void configure(String[] args) {
//        super.configure(args);
//        for (String arg : args) {
//            if ("-background".equalsIgnoreCase(arg)) {
//                System.out.println("*** Working in background mode !");
//                _background = true;
//            }
//        }
//        _service = new CallService(getClass().getSimpleName());
//
//        final C4J _this = this;
//        _service.registerServiceNotification(null, new Runnable() {
//            public void run() {
//                CallImpl4J.shutdown();
//
//                try {
//                    _this.exit();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//
//        _service.configure();
//        try {
//            CallImpl4J.initialize();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        for (String arg : args) {
            if ("-background".equalsIgnoreCase(arg)) {
                System.out.println("*** Working in background mode !");
                _background = true;
            }
        }

        _service = new CallService(getClass().getSimpleName());
        _service.configure();
        CallImpl4JPerf.initialize();
        
    }

//    @Override
//    protected void run() {
//    }
}
