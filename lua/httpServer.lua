--导入timer模块
local timer = require 'timer'

--weedDays星期

weekDays = {7,1,2,3,4,5,6}


Today = 0

function lua_string_split(str, split_char)

    local sub_str_tab = {};

    local i = 0;

    local j = 0;

    while true do
        j = string.find(str, split_char,i+1);

        if j == nil then

            table.insert(sub_str_tab,str);

            break;
        end;

        table.insert(sub_str_tab,tonumber(string.sub(str,i+1,j-1)));

        i = j;
    end
    return sub_str_tab;
end

function getTaskDays(days)

	if(string.len(days) > 1) then

		return lua_string_split(days,',')
	else
		return {tonumber(days)}
	end
	

end


function isFiredToday(days)

	local taskDays = getTaskDays(days)

	for i,day in ipairs(taskDays) do

	print(day,Today)

		if (day == Today) then
		
			return true

		end
	end

	return false

end

--taskArray保存定时任务

taskArray = {}

--taskIds任务ID集合

taskIds = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15}

function getTaskId()

	return table.remove(taskIds,1)
end

function getTask(taskId)

	for i, taskObj in ipairs(taskArray) do

		if (taskObj.id == taskId) then
			
			return {index = i,task = taskObj.obj}

		end
	end

end


-- 初始化GPIO
for i=1,8 do

	gpio.mode(i,gpio.OUTPUT) 
 
	gpio.write(i,gpio.HIGH)	
	
end


--控制GPIO输出电平
function power(deviceId,stat)  

  if stat=="ON"  then 

	gpio.write(deviceId,gpio.LOW) 

  elseif stat=="OFF" then 

	gpio.write(deviceId,gpio.HIGH) 

  end

end


--设置定时器
function setTimer(deviceId,interval,state,days)

	local task = timer.setDailyAlarm(function()

			if (isFiredToday(days)) then

				power(deviceId,state)

			end
		 
		end,tonumber(interval))


	local taskId = getTaskId()

	local taskObj = {id = taskId,obj = task}

	table.insert(taskArray,taskObj)

	return taskId

end

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

--取消定时器
function cancelTimer(taskId)

	local taskRet = getTask(taskId)

	if(taskRet ~= nil) then

		timer.clearInterval(taskRet.task)

		table.remove(taskArray,taskRet.index)

		table.insert(taskIds,1,taskRet.index)
	end

end

-- Create a server
-- and set 30s time out for a inactive client
sv = net.createServer(net.TCP,30)

-- Server listen on 2016
-- Print HTTP headers to console
sv:listen(2016,function(conn)  
    conn:on("receive", function(conn, request)

	if (conn ~= nil) and (request ~= nil) then

		local response,interval,interval1,deviceId,state,taskId,task,days,today

	    print(request)

		response = ""

        if(string.find(request,"time=") ~= nil) then

          _,_,interval = string.find(request,"time=(%d+)&")
        
          print("interval="..interval)
          
		  _,_,deviceId = string.find(request,"deviceId=(%d+)&")
        
          print("GPIO="..deviceId)

          _,_,state = string.find(request,"state=(%a+)")

		  print("state="..state)

		  if (state == "true") then

			state = "ON"

		  elseif (state == "false") then

			state = "OFF"

		  end

		  _,_,days = string.find(request,"days=(.+)&")

    
          timerId = setTimer(deviceId,interval,state,days)


		  print("timerId="..tostring(timerId))

		  response = "timerId="..tostring(timerId)
		
		elseif (string.find(request,"cancelTimer") ~= nil) then

			_,_,taskId = string.find(request,"cancelTimer:(%d+)&")


			cancelTimer(tonumber(taskId))

			response = "cancelTimer=OK"
			 
        elseif (string.find(request, "/power/on") ~= nil) then

			_,_,deviceId = string.find(request,"deviceId=(%d+)&") 

            power(deviceId,"ON")

        elseif (string.find(request, "/power/off") ~= nil) then

			_,_,deviceId = string.find(request,"deviceId=(%d+)&")

            power(deviceId,"OFF")

		elseif (string.find(request,"listDevices")) then	

			--设置星期几并开始循环
			if (Today == 0) then
			
				_,_,interval1 = string.find(request,"interval=(%d+)&")

				_,_,today = string.find(request,"dayOfWeek=(%d+)")

				startWeek(tonumber(interval1),tonumber(today))

			end	

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
 
