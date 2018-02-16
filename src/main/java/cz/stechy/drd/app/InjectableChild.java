package cz.stechy.drd.app;

import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;

/**
 * Rozhraní pro takové kontrolery, do kterých lze injectovat rodičovský kontroler pro přístup
 * k ovládání oken.
 */
public interface InjectableChild {

    /**
     * Injectuje rodičovský kontroler
     *
     * @param parent {@link BaseController}
     */
    void injectParent(BaseController parent);

    default void onScreenResult(int statusCode, int actionId, Bundle bundle) {}

}
