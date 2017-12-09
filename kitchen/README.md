# kitchen #

## Build & Run ##

```sh
$ cd kitchen
$ ./sbt
> jetty:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.


For database, we use mongoDb and there are 3 collections name backup, idpw and main_kitchen.
For the reload part that you asked us to fix, you can test it by increase the refresh time at frontend file monitor.js at line 50 to see whether if the kitchen already change the status but the table has not refresh yet so the table must not be able to change since the information in the database already changed.