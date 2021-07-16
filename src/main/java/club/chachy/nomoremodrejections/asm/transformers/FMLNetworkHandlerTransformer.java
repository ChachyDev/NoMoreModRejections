package club.chachy.nomoremodrejections.asm.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

public class FMLNetworkHandlerTransformer implements ITransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.fml.common.network.internal.FMLNetworkHandler"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("checkModList")) {
                method.instructions.insertBefore(method.instructions.getFirst(), allowMods());
                break;
            }
        }
    }

    private InsnList allowMods() {
        InsnList list = new InsnList();
        list.add(new InsnNode(Opcodes.ACONST_NULL));
        list.add(new InsnNode(Opcodes.ARETURN));
        return list;
    }
}
