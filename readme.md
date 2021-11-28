# http4s Cloud Functions  

Use http4s with Google Cloud Functions. It's a simple wrapper around the http4s-dsl. 

## Usage
You'll find the artifacts in maven central.

```
libraryDependencies += "com.github.ingarabr" % "http4s-cloud-functions" % "<version>"
```

Extend the `Http4sCloudFunctionIOApp` trait and implement the `routes` function. You find a complete example under [docs/example-usage.md](./docs/example-usage.md).

