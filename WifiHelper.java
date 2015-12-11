import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class handle most wifi services
 * @author Álisson Morais
 */
public class WifiHelper {
	private ConnectivityManager cManager;
	private WifiManager wifiManager;

	//Constructor
	public WifiHelper(Context context) {
		//Getting services
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	/**
	 * This method get the state of wifi connection and return a boolean.
	 * @return true if wifi is enabled.
	 */
	public boolean getWifiConnectionState() {
		return wifiManager.isWifiEnabled();
	}

    /**
     * This method set the state of wifi connection.
     * @param check boolean required to set connection state.
     * @return true if the method could set wifi on.
     */
    public boolean setWifiConnectionState(boolean check) {
        return wifiManager.setWifiEnabled(check);
    }

	/**
	 * This method get the connected wifi network name.
	 * @return a string with wifi network name or null.
	 */
	public String getConnectedNetworkSSID() {
		WifiInfo wifiInfo;

        NetworkInfo netInfo = cManager.getActiveNetworkInfo();

		if(netInfo != null) {
			if(netInfo.isConnected()) {
				wifiInfo = wifiManager.getConnectionInfo();
				
				return wifiInfo.getSSID();
			}
		}
		
		return null;
	}
	
	/**
	 * This method get the state of wifi network.
	 * @return true if there is a network connected.
	 */
	public boolean isConnectedTo(String ssid) {
		String wifiName = getConnectedNetworkSSID();

        return (wifiName != null && (wifiName.equals(ssid) || wifiName.equals("\"" + ssid + "\"")));
	}

    /**
     * This methods return all wifi connections available.
     * @return a List of Strings containing all available connections.
     */
	public List<String> listAvailableNetworks() {
		List<String> listNames = new ArrayList<>();
		List<ScanResult> wifiList = wifiManager.getScanResults();
		
		for(ScanResult wConf : wifiList) {
			listNames.add(wConf.SSID);
		}
		
		return listNames;
	}

    /**
     * This methods return all previous configured WiFi connections.
     * @return a List of Strings containing all previous configured connections.
     */
    public List<String> listConfiguredNetworks() {
        List<String> listNames = new ArrayList<>();
        List<WifiConfiguration> wifiList = wifiManager.getConfiguredNetworks();

        for(WifiConfiguration wConf : wifiList) {
            listNames.add(wConf.SSID);
        }

        return listNames;
    }

    /**
     * This methods return both available and configured WiFi connections.
     * @return a List of Strings containing both available and configured connections.
     */
    public List<String> listNetworks() {
        List<String> listNames = new ArrayList<>();

        listNames.addAll(listAvailableNetworks());
        listNames.addAll(listConfiguredNetworks());

        return listNames;
    }

    /**
     * This method calculate signal level of WiFi connections and return it sorted by the strongest.
     * This method uses the result of scan as parameter to get the levels
     * @return List of WiFi connection levels.
     */
    public List<ScanResult> getSignalLevelList() {
        List<ScanResult> wifiScan = wifiManager.getScanResults();
        return getSignalLevelList(wifiScan);
    }

    /**
     * This method calculate signal level of WiFi connections and return it sorted by the strongest.
     * @param wifiList list of WiFi connections that will be checked.
     * @return List of WiFi connection levels.
     */
    public List<ScanResult> getSignalLevelList(List<ScanResult> wifiList) {
        Collections.sort(wifiList, new WifiComparator());

        return wifiList;
    }

    /**
     * This method connect to a specified netId
     * Automatically disable all other connections
     * @param netId of the network to connect
     * @return true if could enable the specified network
     */
    public boolean connectToNetwork(int netId) {
        return connectToNetwork(netId, true);
    }

    /**
     * This method connect to a specified netId
     * @param netId of the network to connect
     * @param disableOthers true to disable all other connections
     * @return true if could enable the specified network
     */
    public boolean connectToNetwork(int netId, boolean disableOthers) {
        return wifiManager.enableNetwork(netId, disableOthers);
    }

    /**
     * This method disconnect from any WiFi connection.
     * @return true if could disconnect from a network.
     */
	public boolean disconnectFromNetwork() {
		return wifiManager.removeNetwork(wifiManager.getConnectionInfo().getNetworkId());
	}
}
