### WiFi_Availability
An application to write the attributes of Wi-Fi access points into a local file. In addition, it download and uploaded an 8 MByte file to an internet server to calculate the upload and download speeds.

### Scanning for WiFi Networks
You can perform a WiFi Network scan like so:
```
List<ScanResult> availableNetworks = new ArrayList<ScanResult>();
                 availableNetworks = activity.wm.getScanResults();
```    
  
### Output


There are three main files: 

1/ All_data : contains the raw data from the ScanResult Android

2/ Individual_AP_Tributes : contains refined data : 
Data,longitude,latitude,BSSID,RSSI,BSSID(first 10 characteristics),SSID,capabilities,frequency,timestamp,isPasspointNetwork,channelWidth,centerFreq0,centerFreq1, is80211mcResponder

3/ Extra_Information : contains information about the available networks
Data,longitude,latitude,Number of total Wi-Fi networks,Number of open networks,max RSSI level,average RSSI level,min RSSU level,connected RSSI level,frequency,bandwidth,Mode,utilization,BSSID,SSID,download speed,upload speed
