package io.github.qishr.cascara.format.vsix;

import io.github.qishr.cascara.common.diagnostic.AbstractLocalizableException;
import io.github.qishr.cascara.common.diagnostic.LocalizableRuntimeException;
import io.github.qishr.cascara.common.diagnostic.code.DiagnosticCode;

public class VsixException extends LocalizableRuntimeException {

    public VsixException(DiagnosticCode code, Object... details) {
        this(null, code, details);
    }

    public VsixException(Throwable cause, DiagnosticCode code, Object... details) {
        super(cause, code, details);
    }
}
