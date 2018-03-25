This project throttles a client's request in a distributed environment.

The client can have different rate limit configs. The rate limit periods are defined in com.ishan.base.RateLimitPeriod.

Logic:

This uses redis as a store for getting the current limits. Whenever a request comes, com.ishan.filter.RateLimitFilter asks com.ishan.base.RateLimitValidator.validateRateLimited(com.ishan.base.ClientConfig, com.ishan.base.RequestDetails) whether the current request adheres to the rate limit or not.

RateLimitValidator first constructs the redis keys. There can be many keys for a request. The client configurations are stored in com.ishan.base.ClientConfig. This assumes redis as store for client config too.

Lets take a sample clientConfig:

{
  "clientId": "ecom",
  "limit": {
    "SECOND": 10,
    "MINUTE": 100
  },
  "endPoint": {
    "price": {
      "SECOND": 5
    }
  }
}

There will be 3 keys constructed. Lets say the request came at 5 minutes and 10 seconds & 100 milliseconds. They are wrapped to the nearest unit. So a request coming in on a Wednesday would be wrapped to Sunday while calculating the weekly limits.

The keys would look something like:

ecom_second_5 <br>
ecom_minute_10 <br>
ecom_second_price_5

We are using <b>Redis Pipelines</b> to reduce the Round Trip Time to the redis server.

We increment the count of the following keys by 1 and <b>also set the expiry time</b> so that clean up is performed automatically by redis. 

