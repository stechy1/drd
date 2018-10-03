package cz.stechy.drd.processor.dao;

public class Entry {

    private final String _package;
    private final String _className;
    private final String _implementation;

    Entry(String _package, String _className, String _implementation) {
        this._package = _package;
        this._className = _className;
        this._implementation = _implementation;
    }

    public String getPackage() {
        return _package;
    }

    public String getClassName() {
        return _className;
    }

    public String getImplementation() {
        return _implementation;
    }
}
