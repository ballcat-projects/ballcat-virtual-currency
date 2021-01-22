# 比特转账使用提示

当前版本 bitcoinj 不支持 p2sh-p2wpkh脚本, 所以在 https://github.com/bitcoinj/bitcoinj/pull/1987 被合并之前. 无法使用 p2sh-p2wpkh 类型地址转账

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