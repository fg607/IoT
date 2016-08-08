-- Print IP address
ip = wifi.sta.getip()  
print(ip)

-- Configure the ESP as a station (client)
wifi.setmode(wifi.STATION)  
wifi.sta.config("hw-pa", "fg02081523")  
wifi.sta.autoconnect(1)
