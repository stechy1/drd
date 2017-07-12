package cz.stechy.drd.controller;

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
     * @param baseController {@link BaseController}
     */
    void injectParent(BaseController baseController);

    void onScreenResult(int statusCode, int actionId, Bundle bundle);

}
