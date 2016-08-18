--点亮芯片灯
gpio.mode(0, gpio.OUTPUT)
gpio.write(0, gpio.LOW)

-- 初始化GPIO
for i=1,8 do

	gpio.mode(i,gpio.OUTPUT) 
	gpio.write(i,gpio.HIGH)	
	
end


dofile("connectWifi.lc")

dofile("httpServer.lc")

