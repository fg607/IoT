local timer = require 'timer'
local func = require 'function'

local Today = 0
local serverConn

local initing = false

function startWeek(interval,today)

	Today = today

	timer.setDailyAlarm(function()

		if (Today < 7) then

			Today = Today +1
		else

			Today = 1

		end

	end,tonumber(interval))

end

--taskArray保存定时任务
local taskArray = {}

--取消定时器
function cancelTimer(taskId)

	local taskRet = getTask(taskId)

	if(taskRet ~= nil) then

		timer.clearInterval(taskRet.task)

		table.remove(taskArray,taskRet.index)

		table.insert(taskIds,1,taskRet.index)
	end

end

--设置定时器
function setTimer(taskId,deviceId,interval,state,days)

	local task = timer.setDailyAlarm(function()

			if (isFiredToday(days)) then

				func.power(deviceId,state)

			end
		 
		end,tonumber(interval))

	local taskObj = {id = taskId,obj = task}

	table.insert(taskArray,taskObj)

end

function getTask(taskId)

	for i, taskObj in ipairs(taskArray) do

		if (taskObj.id == taskId) then
			
			return {index = i,task = taskObj.obj}

		end
	end

end

function isFiredToday(days)

	local taskDays = func.getTaskDays(days)

	for i,day in ipairs(taskDays) do

		if (day == Today) then
		
			return true

		end
	end

	return false

end

function initTask(taskInfo)

	initing = true
	local week,currentHour,currentMinute = string.match(taskInfo,"week=(%d+)&hour=(%d+)&minute=(%d+)&")
	local i,j = 0,0
	local sub
	local _,i = string.find(taskInfo, "&\n",i+1)

	if(i == nil) then
		initing = false
		return
	end

	startWeek(func.getWeekInterval(currentHour,currentMinute),tonumber(week)+1)
			
	while true do

		_,j = string.find(taskInfo,"\n",i+1) 
        
		if j == nil then
			break
		end

		sub = string.sub(taskInfo,i+1,j-1);

		if (string.len(sub) > 10) then
					
			local taskId,deviceId,hour,minute,state,days = string.match(
		  				sub,"taskId=(%d+)&deviceId=(%d+)&hour=(%d+)&minute=(%d+)&state=(%a+)&days=(.+)")
			local interval = func.getTaskInterval(currentHour,currentMinute,hour,minute)
			setTimer(taskId,deviceId,interval,state,days)
			print("recover task:",taskId,deviceId,interval,state,days)
					
		end
		i = j;
	end

	initing = false
	serverConn:close()
	serverConn = nil
	collectgarbage()
	
end

function recTaskFromServer()

	local post_request = "POST /gettask HTTP/1.1\r\n"
    	.."Host: fg.xdty.org:8000\r\n"
		.."Connection: keep-alive\r\n"
		.."Accept: text/plain, */*\r\n"
    	.. "Content-Type: text/plain\r\n"
		.."Content-Length: 0\r\n\r\n"

	serverConn=net.createConnection(net.TCP, 0)  

	serverConn:on("receive",  function(sck, response) 

					if (Today == 0) and (initing == false) then 
						initTask(response) 
					end 
			end) 

	serverConn:connect(8000,"fg.xdty.org")  

	serverConn:on("connection",function(obj) 
   		obj:send(post_request)
		end)

end

recTaskFromServer()

local sv = net.createServer(net.TCP,30)

sv:listen(2016,function(conn)  

    conn:on("receive", function(conn, request)

	if (conn ~= nil) and (request ~= nil) then

	    print(request)

		local response,hour,minute,interval,interval1,deviceId,state,days,taskId

		response = ""

        if(string.find(request,"setTimer") ~= nil) then


		  taskId,deviceId,hour,minute,interval,days,state = string.match(

		  		request,"taskId=(%d+)&deviceId=(%d+)&hour=(%d+)&minute=(%d+)&interval=(%d+)&days=(.+)&state=(%a+)")

		  if (state == "true") then

			state = "ON"

		  elseif (state == "false") then

			state = "OFF"

		  end

          setTimer(taskId,deviceId,interval,state,days)

		  func.saveTaskToServer(taskId,deviceId,hour,minute,state,days)

		  response = "setTimer=OK"

		
		elseif (string.find(request,"cancelTimer") ~= nil) then

			_,_,taskId = string.find(request,"taskId=(%d+)&")

			cancelTimer(tonumber(taskId))

			func.deleteServerTask(taskId)

			response = "cancelTimer=OK"
			 
        elseif (string.find(request, "/power/on") ~= nil) then

			_,_,deviceId = string.find(request,"deviceId=(%d+)&") 

            func.power(deviceId,"ON")

        elseif (string.find(request, "/power/off") ~= nil) then

			_,_,deviceId = string.find(request,"deviceId=(%d+)&")

            func.power(deviceId,"OFF")

		elseif (string.find(request,"initTask")) then

			if (Today == 0) and (initing == false) then
			
				initTask(request)
			end

		elseif (string.find(request,"listDevices")) then	

			--返回GPIO信息		
			for i=1,8 do

				response = response..i..":"..gpio.read(i)..";"

			end

        end

        conn:send("HTTP/1.1 200 OK\n\n"..response)

	end
    end)

	conn:on("sent",function(conn)
		conn:close()
        conn = nil
		collectgarbage() 
		end)
end) 
 
