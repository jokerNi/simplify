package org.cf.smalivm.opcode;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.Utils;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction22c;
import org.jf.dexlib2.util.ReferenceUtil;

public class NewArrayOp extends MethodStateOp {

    static NewArrayOp create(Instruction instruction, int address, VirtualMachine vm) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        Instruction22c instr = (Instruction22c) instruction;
        int destRegister = instr.getRegisterA();
        int dimensionRegister = instr.getRegisterB();

        // [[Lsome_class;
        String typeReference = ReferenceUtil.getReferenceString(instr.getReference());

        String baseClassName = typeReference.replace("[", "");
        boolean isLocalClass = vm.isLocalClass(baseClassName);

        return new NewArrayOp(address, opName, childAddress, destRegister, dimensionRegister, typeReference,
                        isLocalClass);
    }

    private final int destRegister;
    private final int dimensionRegister;
    private final boolean isLocalClass;
    private final String typeReference;

    private NewArrayOp(int address, String opName, int childAddress, int destRegister, int dimensionRegister,
                    String typeReference, boolean isLocalClass) {
        super(address, opName, childAddress);

        this.destRegister = destRegister;
        this.dimensionRegister = dimensionRegister;
        this.typeReference = typeReference;
        this.isLocalClass = isLocalClass;
    }

    @Override
    public int[] execute(MethodState mState) {
        Object dimensionValue = mState.readRegister(dimensionRegister);

        Object instance = null;
        if (dimensionValue instanceof UnknownValue) {
            instance = new UnknownValue(typeReference);
        } else {
            int dimension = (int) dimensionValue;
            try {
                instance = Utils.getArrayInstanceFromSmaliTypeReference(typeReference, dimension, isLocalClass);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        mState.assignRegister(destRegister, instance);

        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(destRegister).append(", r").append(dimensionRegister).append(", ").append(typeReference);

        return sb.toString();
    }
}
