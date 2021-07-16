package club.chachy.nomoremodrejections.asm.launchwrapper;

import club.chachy.nomoremodrejections.asm.transformers.FMLNetworkHandlerTransformer;
import club.chachy.nomoremodrejections.asm.transformers.ITransformer;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

public class ClassTransformer implements IClassTransformer {

    // create a logger to distinguish our errors from a normal error
    private static final Logger LOGGER = LogManager.getLogger("NoMoreModRejections");

    // create a map of transformers
    private final Multimap<String, ITransformer> transformerMap = ArrayListMultimap.create();

    // make a jvm flag that could be used to dump transformed classes
    // usable by adding -DdebugBytecode=true to the jvm arguments
    public static final boolean outputBytecode = Boolean.parseBoolean(System.getProperty("debugBytecode", "false"));

    public ClassTransformer() {
        registerTransformer(new FMLNetworkHandlerTransformer());
    }

    private void registerTransformer(ITransformer transformer) {
        for (String cls : transformer.getClassName()) {
            transformerMap.put(cls, transformer);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes == null) return null;

        Collection<ITransformer> transformers = transformerMap.get(transformedName);
        if (transformers.isEmpty()) return bytes;

        ClassReader reader = new ClassReader(bytes);
        ClassNode node = new ClassNode();
        reader.accept(node, ClassReader.EXPAND_FRAMES);

        for (ITransformer transformer : transformers) {
            transformer.transform(node, transformedName);
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        try {
            node.accept(writer);
        } catch (Throwable t) {
            LOGGER.error("Exception when transforming " + transformedName + " : " + t.getClass().getSimpleName());
            t.printStackTrace();
        }

        if (outputBytecode) {
            File bytecodeDirectory = new File("bytecode");
            String transformedClassName;

            if (transformedName.contains("$")) {
                transformedClassName = transformedName.replace('$', '.') + ".class";
            } else {
                transformedClassName = transformedName + ".class";
            }

            if (!bytecodeDirectory.exists()) {
                bytecodeDirectory.mkdirs();
            }

            File bytecodeOutput = new File(bytecodeDirectory, transformedClassName);

            try {
                if (!bytecodeOutput.exists()) {
                    bytecodeOutput.createNewFile();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try (FileOutputStream os = new FileOutputStream(bytecodeOutput)) {
                os.write(writer.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return writer.toByteArray();
    }
}
