package org.cf.smalivm.emulate;

import org.cf.smalivm.SideEffect.Level;
import org.cf.smalivm.context.MethodState;

public interface EmulatedMethod {

    public void execute(MethodState mState) throws Exception;

    public Level getSideEffectLevel();

}
