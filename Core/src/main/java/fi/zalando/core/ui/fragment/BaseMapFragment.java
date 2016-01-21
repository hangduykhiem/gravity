package fi.zalando.core.ui.fragment;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fi.zalando.core.utils.Preconditions;

/**
 * Base fragment to wrap all together some utility methods for fragments
 *
 * Created by jduran on 03/12/15.
 */
public abstract class BaseMapFragment extends BaseFragment implements OnMapReadyCallback {

    MapView mapView;

    /**
     * Lifecycle method
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = super.onCreateView(inflater, container, savedInstanceState);
        Preconditions.checkNotNull(fragmentView);
        mapView = (MapView)fragmentView.findViewById(getMapId());
        mapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity());
        //Prepare the map. It can be used after onMapReady is called.
        mapView.getMapAsync(this);
        // return inflated view
        return fragmentView;
    }

    /**
     * Lifecycle method
     */
    @Override
    public void onResume() {

        mapView.onResume();
        super.onResume();
    }

    /**
     * Lifecycle method
     */
    @Override
    public void onDestroy() {

        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * Lifecycle method
     */
    @Override
    public void onPause() {

        super.onPause();
        mapView.onPause();
    }

    /**
     * Lifecycle method
     */
    @Override
    public void onLowMemory() {

        super.onLowMemory();
        mapView.onLowMemory();
    }

    /**
     * Lifecycle method
     *
     * @param outState {@link Bundle} where the state of the activity is stored
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {

        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }


    /**
     * Abstract method that provides the resource id for the map within the layout given in
     * getSubFragmentLayoutId.
     *
     * @return Resource id for the map within the current layout.
     */
    @IdRes
    protected abstract int getMapId();

}
