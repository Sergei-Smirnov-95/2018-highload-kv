id = 0
wrk.method = "PUT"
wrk.body = "myData"
request = function()
	path = "/v0/entity?id=" .. id
	id = id + 1
	return wrk.format(nil, path)
end
