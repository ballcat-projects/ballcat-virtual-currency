# 比特转账使用提示

由于我目前使用的获取未花费输出接口不支持 原生隔离地址, 所以 P2WPKH 相关我没有进行测试, 如果要使用,请先进行测试

# 功能

1. 支持三种平台交易查询(支持多种abi转账方法处理, 可自定义新的abi方法处理)
2. 支持 Etherscan 转账(ETH, 以及合约转账)
3. 支持 Tronscan 转账(TRX, TRC10 以及 TRC20 转账)
3. 支持 BTC 转账
4. 支持 OMNI 中各个合约转账

# 使用

maven

```xml

<dependency>
    <groupId>live.lingting</groupId>
    <artifactId>virtual-currency</artifactId>
    <version>releases中最新版本号</version>
</dependency>
```