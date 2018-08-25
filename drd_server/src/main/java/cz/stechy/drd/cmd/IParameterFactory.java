package cz.stechy.drd.cmd;

public interface IParameterFactory {

    /**
     * Vrátí existující instanci třídy {@link IParameterProvider}
     * Je potřeba nejdříve zavolat metodu {@code getParameters(String[] args}, aby se instance vytvořila
     *
     * @return {@link IParameterProvider}
     */
    IParameterProvider getParameters();

    /**
     * Vytvoří instanci třídy {@link IParameterProvider}
     *
     * @param args Argumenty z příkazové řádky
     * @return {@link IParameterProvider}
     */
    IParameterProvider getParameters(String[] args);
}
