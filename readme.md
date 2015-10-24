# Modern HttpClient (Java 6+)

[![License](https://img.shields.io/github/license/mntone/HttpClient.svg?style=flat-square)](https://github.com/mntone/HttpClient/blob/master/LICENSE.txt)

It’s Modern HttpClient for JVM. It just likes .NET HttpClient.


## Requirement

- [jsr166e](http://g.oswego.edu/dl/concurrency-interest/)


## Usage

### Get content as `String`

It’s very simple.

```java
final HttpClient client = new HttpClient();
try
{
	final String responseText = client.getStringAsync("http://mntone.minibird.jp/").get();
	System.out.print(responseText);
}
catch (Exception e) { }
```

## LICENSE

[MIT License](https://github.com/mntone/HttpClient/blob/master/LICENSE.txt)


## Contributing

1. Fork it.
2. Create your feature branch. (`git checkout -b NEW_FEATURE_BRANCH_NAME`) ← **important!**
3. Commit, push, and pull request!


## Author

- mntone<br>
	GitHub: https://github.com/mntone<br>
	Twitter: https://twitter.com/mntone (posted in Japanese; however, english is ok)