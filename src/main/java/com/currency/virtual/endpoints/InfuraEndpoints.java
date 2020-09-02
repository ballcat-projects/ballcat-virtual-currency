package com.currency.virtual.endpoints;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Infura节点
 *
 * @author lingting 2020-09-01 16:56
 */
@Getter
@AllArgsConstructor
public enum InfuraEndpoints implements Endpoints {
    /**
     * 节点名
     */
    MAINNET("https://mainnet.infura.io/v3/", "主节点"),
    ROPSTEN("https://ropsten.infura.io/v3/", "测试"),
    KOVAN("https://kovan.infura.io/v3/", ""),
    RINKEBY("https://rinkeby.infura.io/v3/", ""),
    /*
     * 下面这个节点实际名称 GÖRLI, 由于该名称存在非ascii字符，改名
     */
    GOERLI("https://goerli.infura.io/v3/", ""),
    ;
    private final String http;
    private final String desc;
}
