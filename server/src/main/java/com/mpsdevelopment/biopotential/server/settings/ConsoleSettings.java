package com.mpsdevelopment.biopotential.server.settings;

import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

public class ConsoleSettings {
    public ConsoleSettings() {
        System.setErr(LoggerUtil.getRedirectedToLoggerErrPrintStream(System.err));
        System.setOut(LoggerUtil.getRedirectedToLoggerOutPrintStream(System.out));
    }
}
