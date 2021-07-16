package club.chachy.nomoremodrejections.asm.plugin;

import club.chachy.nomoremodrejections.asm.launchwrapper.ClassTransformer;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

// forge warns when this annotation is missing so make it shut up
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
public class NoMoreModRejectionsFMLLoadingPlugin implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{ClassTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
