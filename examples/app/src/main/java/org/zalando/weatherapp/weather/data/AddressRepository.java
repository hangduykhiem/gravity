package org.zalando.weatherapp.weather.data;

import android.content.Context;
import android.location.Address;
import android.location.Location;
import com.google.android.gms.location.LocationRequest;
import com.patloew.rxlocation.RxLocation;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import javax.inject.Inject;
import org.zalando.core.data.BaseRepository;

public class AddressRepository extends BaseRepository {

  private Context context;

  @Inject
  public AddressRepository(Context context) {
    this.context = context;
  }

  public Observable<Address> fetchCurrentAddress() throws SecurityException{

    LocationRequest request = LocationRequest.create()
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        .setInterval(5000);

    final RxLocation rxLocation = new RxLocation(context);

    return rxLocation
        .location()
        .updates(request)
        .flatMap(new Function<Location, ObservableSource<Address>>() {
          @Override
          public ObservableSource<Address> apply(@NonNull Location location) throws Exception {
            return rxLocation.geocoding().fromLocation(location).toObservable();
          }
        });
  }
}
