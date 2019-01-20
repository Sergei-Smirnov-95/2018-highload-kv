Профилирование приложения
--
Cоберём нагрузочный инструмент wrk и напишем функцию генерации PUT запросов:
```lua
id = 0

wrk.method = "PUT"

wrk.body = "myData"

request = function()

path = "/v0/entity?id=" .. id

id = id + 1

return wrk.format(nil, path)

end
```
После запуска написанного скрипта в wrk с ключами 4 потоков и 4-х соединений в течении 1 минуты получаем следующие результаты:

```sergei@Sergei-System:~/prifiling/wrk/wrk$ wrk --latency -c4 -t4 -d1m -s scripts/put_req.lua http://localhost:8080

Running 1m test @ http://localhost:8080

4 threads and 4 connections

Thread Stats Avg Stdev Max +/- Stdev

Latency 146.97us 362.04us 14.35ms 97.15%

Req/Sec 8.35k 2.17k 12.57k 53.38%

Latency Distribution

50% 78.00us

75% 145.00us

90% 165.00us

99% 1.61ms

1993999 requests in 1.00m, 127.41MB read

Requests/sec: 33227.08

Transfer/sec: 2.12MB
```
Результат профилирования в этот запуск приведён на wrk_put
![wrk_put](https://github.com/Sergei-Smirnov-95/2018-highload-kv/blob/master/profiling/wrk_put.svg)
Аналогичным образом напишем скрипт get_req.lua для GET запросов и прочитаем записанные данные по тем же ключам:

```lua
id = 0

wrk.method = "GET"

request = function()

path = "/v0/entity?id=" .. id

id = id + 1

return wrk.format(nil, path)

end
```
Результаты исполнения в wrk представлены ниже:
```
sergei@Sergei-System:~/prifiling/wrk/wrk$ wrk --latency -c4 -t4 -d1m -s scripts/get_req.lua http://localhost:8080

Running 1m test @ http://localhost:8080

4 threads and 4 connections

Thread Stats Avg Stdev Max +/- Stdev

Latency 600.12us 1.38ms 25.62ms 90.23%

Req/Sec 5.84k 2.68k 13.61k 69.58%

Latency Distribution

50% 100.00us

75% 253.00us

90% 1.92ms

99% 6.88ms

1396766 requests in 1.00m, 1.42GB read

Requests/sec: 23241.74

Transfer/sec: 24.14MB

sergei@Sergei-System:~/prifiling/wrk/wrk$
```
Результаты профилирования приведены на рисунке get_wrk
![get_wrk](https://github.com/Sergei-Smirnov-95/2018-highload-kv/blob/master/profiling/get_wrk.svg)
Аналогичным образом пишем скрипт для удаления данных и запускаем в wrk:

```lua
id = 0

wrk.method = "DELETE"

request = function()

path = "/v0/entity?id=" .. id

id = id + 1

return wrk.format(nil, path)

end
```
Результаты запуска:
```
sergei@Sergei-System:~/prifiling/wrk/wrk$ wrk --latency -c4 -t4 -d1m -s scripts/del_req.lua http://localhost:8080

Running 1m test @ http://localhost:8080

4 threads and 4 connections

Thread Stats Avg Stdev Max +/- Stdev

Latency 525.59us 1.18ms 23.03ms 92.73%

Req/Sec 3.91k 1.63k 11.23k 68.79%

Latency Distribution

50% 201.00us

75% 351.00us

90% 0.96ms

99% 6.34ms

932978 requests in 1.00m, 60.50MB read

Requests/sec: 15541.39

Transfer/sec: 1.01MB

sergei@Sergei-System:~/prifiling/wrk/wrk$
```
Результаты работы async-profiler для DELETE запрсов представлен ниже:
![wrk_del](https://github.com/Sergei-Smirnov-95/2018-highload-kv/blob/master/profiling/del_wrk.svg)
Из результатов профилирования можно сделать следующие выводы:
 * При выполнении PUT и DELETE запросов значительная часть времени (около 45%) тратится непосредственно на 
выполнение запроса сохранения данных в RocksDB. В случае с GET запросом расходы на работу хранилища составляют около 30%. 
 * Расходы по доставанию параметров запроса из заголовка составляют около 1% времени,также как и на их преобразования
 * Около 30% времени уходит на отправку http ответов. В случе GET запросов- около 40% времени.
