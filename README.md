# 功能

1. 支持三种平台交易查询(支持多种abi转账方法处理, 可自定义新的abi方法处理)
2. 支持 Etherscan 转账(ETH, 以及合约转账)
3. 支持 Tronscan 转账(TRX, TRC10 以及 TRC20 转账)
3. 支持 BTC 转账
4. 支持 OMNI 中各个合约转账

# 通过maven使用

```xml
<!-- 需要全部 -->
<dependency>
    <groupId>live.lingting</groupId>
    <artifactId>virtual-currency-all</artifactId>
    <version>releases中最新版本号</version>
</dependency>

<!-- 仅需要 bitcoin -->
<dependency>
    <groupId>live.lingting</groupId>
    <artifactId>virtual-currency-bitcoin</artifactId>
    <version>releases中最新版本号</version>
</dependency>

<!-- 仅需要 etherscan -->
<dependency>
    <groupId>live.lingting</groupId>
    <artifactId>virtual-currency-etherscan</artifactId>
    <version>releases中最新版本号</version>
</dependency>

<!-- 仅需要 tronscan -->
<dependency>
    <groupId>live.lingting</groupId>
    <artifactId>virtual-currency-tronscan</artifactId>
    <version>releases中最新版本号</version>
</dependency>
```

# 结构

```text
├─virtual-currency-bitcoin
│  ├─src
│  │  ├─main
│  │  │  ├─contract 合约枚举
│  │  |  ├─endpoints 节点
│  │  |  ├─enums 枚举
│  │  │  ├─model api返回结果包装
│  │  │  ├─properties 配置
│  │  │  └─util 工具类
│  │  └─test 测试用例
├─virtual-currency-core
│  ├─src
│  │  ├─main
│  │  │  ├─bip bip实现
│  │  |  ├─enums 枚举
│  │  │  ├─exception 异常
│  │  │  ├─model 常用数据包装
│  │  │  └─util 工具类
│  │  └─test 测试用例
├─virtual-currency-etherscan
│  ├─src
│  │  ├─main
│  │  │  ├─contract 合约枚举
│  │  |  ├─endpoints 节点
│  │  |  ├─enums 枚举
│  │  │  ├─model api返回结果包装
│  │  │  ├─properties 配置
│  │  │  └─util 工具类
│  │  └─test 测试用例
└─virtual-currency-tronscan
    ├─src
       ├─main
          ├─contract 合约枚举
          ├─endpoints 节点
          ├─enums 枚举
          ├─model api返回结果包装
          ├─properties 配置
          └─util 工具类
```