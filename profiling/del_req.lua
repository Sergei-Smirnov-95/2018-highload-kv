id = 0
wrk.method = "DELETE"
request = function()
	path = "/v0/entity?id=" .. id
	id = math.random(99999999)
	return wrk.format(nil, path)
end
