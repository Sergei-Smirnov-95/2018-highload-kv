id = 0
wrk.method = "GET"
request = function()
	path = "/v0/entity?id=" .. id
	id = math.random(9999999)
	return wrk.format(nil, path)
end
