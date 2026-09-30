package io.github.qishr.cascara.format.vsix;

import io.github.qishr.cascara.common.diagnostic.AbstractLocalizableException;
import io.github.qishr.cascara.common.diagnostic.LocalizableRuntimeException;
import io.github.qishr.cascara.common.diagnostic.message.DiagnosticMessage;

public class VsixException extends LocalizableRuntimeException {

    public VsixException(DiagnosticMessage code, Object... details) {
        this(null, code, details);
    }

    public VsixException(Throwable cause, DiagnosticMessage code, Object... details) {
        super(cause, code, details);
    }
}
