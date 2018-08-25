package cz.stechy.drd.cmd;

import com.google.inject.Singleton;

@Singleton
public class ParameterFactory implements IParameterFactory {

    private IParameterProvider provider;

    @Override
    public IParameterProvider getParameters() {
        return provider;
    }

    @Override
    public IParameterProvider getParameters(String[] args) {
        if (provider == null) {
            provider = new CmdParser(args);
        }

        return provider;
    }
}
