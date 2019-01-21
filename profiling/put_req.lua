id = 0
wrk.method = "PUT"
wrk.body = "myData:wlefiqelrfknq;:LI:KJD;riujew;flkwj:LJKHJEVWKkjdcbhelwikube;l249187zz"
request = function()
	path = "/v0/entity?id=" .. id
	id = math.random(99999999)
	return wrk.format(nil, path)
end
