local func = {}

function func.getWeekInterval(currentHour,currentMinute)

	local interval = 24*60*60*1000 - tonumber(currentHour)*60*60*1000 - tonumber(currentMinute)*60*1000
	return interval
end

function func.getTaskInterval(currentHour,currentMinute,hour,minute)

	local now = tonumber(currentHour)*60*60*1000 + tonumber(currentMinute)*60*1000
	local alarmTime = tonumber(hour)*60*60*1000 + tonumber(minute)*60*1000
	local interval = 0
	
	if(alarmTime > now) then

		interval = alarmTime - now
	else

		interval = 24*60*60*1000+alarmTime-now
	end

	return interval
end

--字符串分割
function func.lua_string_split(str, split_char)

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


function func.getTaskDays(days)

	if(string.len(days) > 1) then

		return func.lua_string_split(days,',')
	else
		return {tonumber(days)}
	end

end

--控制GPIO输出电平
function func.power(deviceId,stat)  

  if stat=="ON"  then 

	gpio.write(deviceId,gpio.LOW) 

  elseif stat=="OFF" then 

	gpio.write(deviceId,gpio.HIGH) 

  end
end


function func.saveTaskToServer(taskId,deviceId,hour,minute,state,days)

	local s = "taskId="..taskId.."&deviceId="..deviceId.."&hour="
		
		..hour.."&minute="..minute.."&state="..state.."&days="..days

	local post_request = "POST /savetask HTTP/1.1\r\n"
    	.."Host: fg.xdty.org:8000\r\n"
		.."Connection: keep-alive\r\n"
		.."Accept: text/plain, */*\r\n"
    	.. "Content-Type: text/plain\r\n"
		.."Content-Length: " .. string.len(s) .. "\r\n\r\n"
		..s

	local conn=net.createConnection(net.TCP, 0)  

	conn:on("receive",  function(sck, response)
				print(response)
				if(string.find(response,"saveTask is ok") ~= nil) then	
					conn:close()
					conn = nil
				end
			end) 

	conn:connect(8000,"fg.xdty.org")  

	conn:on("connection",function(obj) 
   		obj:send(post_request)
		end)

end

function func.deleteServerTask(taskId)

	local s = "taskId="..taskId.."&"

	print("del"..s)
	local post_request = "POST /deletetask HTTP/1.1\r\n"
    	.."Host: fg.xdty.org:8000\r\n"
		.."Connection: keep-alive\r\n"
		.."Accept: text/plain, */*\r\n"
    	.. "Content-Type: text/plain\r\n"
		.."Content-Length: " .. string.len(s).."\r\n\r\n"
		..s

	local conn=net.createConnection(net.TCP, 0)  

	conn:on("receive",  function(sck, response)
				print(response)	
				if(string.find(response,"deleteTask is ok") ~= nil) then	
					conn:close()
					conn = nil
				end
			end) 

	conn:connect(8000,"fg.xdty.org")  

	conn:on("connection",function(obj) 
   		obj:send(post_request)
		end)


end

return func
