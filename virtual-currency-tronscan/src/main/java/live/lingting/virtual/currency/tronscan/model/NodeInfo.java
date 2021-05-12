package live.lingting.virtual.currency.tronscan.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import live.lingting.virtual.currency.tronscan.properties.TronscanProperties;
import live.lingting.virtual.currency.tronscan.util.TronscanModelUtils;

/**
 * @author lingting 2021/2/26 18:13
 */
@NoArgsConstructor
@Data
public class NodeInfo {

	@JsonProperty("activeConnectCount")
	private Long activeConnectCount;

	@JsonProperty("beginSyncNum")
	private Long beginSyncNum;

	@JsonProperty("block")
	private String block;

	@JsonProperty("cheatWitnessInfoMap")
	private Map<String, String> cheatWitnessInfoMap;

	@JsonProperty("configNodeInfo")
	private ConfigNodeInfo configNodeInfo;

	@JsonProperty("currentConnectCount")
	private Long currentConnectCount;

	@JsonProperty("machineInfo")
	private MachineInfo machineInfo;

	@JsonProperty("passiveConnectCount")
	private Long passiveConnectCount;

	@JsonProperty("peerList")
	private List<PeerList> peerList;

	@JsonProperty("solidityBlock")
	private String solidityBlock;

	@JsonProperty("totalFlow")
	private Long totalFlow;

	@SneakyThrows
	public static NodeInfo of(TronscanProperties properties) {
		return TronscanModelUtils.get(properties, "wallet/getnodeinfo", NodeInfo.class);
	}

	@NoArgsConstructor
	@Data
	public static class ConfigNodeInfo {

		@JsonProperty("activeNodeSize")
		private Integer activeNodeSize;

		@JsonProperty("allowAdaptiveEnergy")
		private Integer allowAdaptiveEnergy;

		@JsonProperty("allowCreationOfContracts")
		private Integer allowCreationOfContracts;

		@JsonProperty("backupListenPort")
		private Integer backupListenPort;

		@JsonProperty("backupMemberSize")
		private Integer backupMemberSize;

		@JsonProperty("backupPriority")
		private Integer backupPriority;

		@JsonProperty("codeVersion")
		private String codeVersion;

		@JsonProperty("dbVersion")
		private Integer dbVersion;

		@JsonProperty("discoverEnable")
		private Boolean discoverEnable;

		@JsonProperty("listenPort")
		private Integer listenPort;

		@JsonProperty("maxConnectCount")
		private Integer maxConnectCount;

		@JsonProperty("maxTimeRatio")
		private Double maxTimeRatio;

		@JsonProperty("minParticipationRate")
		private Integer minParticipationRate;

		@JsonProperty("minTimeRatio")
		private Double minTimeRatio;

		@JsonProperty("p2pVersion")
		private String p2pVersion;

		@JsonProperty("passiveNodeSize")
		private Integer passiveNodeSize;

		@JsonProperty("sameIpMaxConnectCount")
		private Integer sameIpMaxConnectCount;

		@JsonProperty("sendNodeSize")
		private Integer sendNodeSize;

		@JsonProperty("supportConstant")
		private Boolean supportConstant;

		@JsonProperty("versionNum")
		private String versionNum;

	}

	@NoArgsConstructor
	@Data
	public static class MachineInfo {

		@JsonProperty("cpuCount")
		private Long cpuCount;

		@JsonProperty("cpuRate")
		private Double cpuRate;

		@JsonProperty("deadLockThreadCount")
		private Long deadLockThreadCount;

		@JsonProperty("deadLockThreadInfoList")
		private List<Object> deadLockThreadInfoList;

		@JsonProperty("freeMemory")
		private BigInteger freeMemory;

		@JsonProperty("javaVersion")
		private String javaVersion;

		@JsonProperty("jvmFreeMemory")
		private Long jvmFreeMemory;

		@JsonProperty("jvmTotalMemory")
		private Long jvmTotalMemory;

		@JsonProperty("memoryDescInfoList")
		private List<MemoryDescInfoList> memoryDescInfoList;

		@JsonProperty("osName")
		private String osName;

		@JsonProperty("processCpuRate")
		private BigDecimal processCpuRate;

		@JsonProperty("threadCount")
		private Long threadCount;

		@JsonProperty("totalMemory")
		private Long totalMemory;

		@NoArgsConstructor
		@Data
		public static class MemoryDescInfoList {

			@JsonProperty("initSize")
			private BigInteger initSize;

			@JsonProperty("maxSize")
			private BigInteger maxSize;

			@JsonProperty("name")
			private String name;

			@JsonProperty("useRate")
			private BigDecimal useRate;

			@JsonProperty("useSize")
			private BigInteger useSize;

		}

	}

	@NoArgsConstructor
	@Data
	public static class PeerList {

		@JsonProperty("active")
		private Boolean active;

		@JsonProperty("avgLatency")
		private BigDecimal avgLatency;

		@JsonProperty("blockInPorcSize")
		private BigInteger blockInPorcSize;

		@JsonProperty("connectTime")
		private Long connectTime;

		@JsonProperty("disconnectTimes")
		private Long disconnectTimes;

		@JsonProperty("headBlockTimeWeBothHave")
		private Long headBlockTimeWeBothHave;

		@JsonProperty("headBlockWeBothHave")
		private String headBlockWeBothHave;

		@JsonProperty("host")
		private String host;

		@JsonProperty("inFlow")
		private Long inFlow;

		@JsonProperty("lastBlockUpdateTime")
		private Long lastBlockUpdateTime;

		@JsonProperty("lastSyncBlock")
		private String lastSyncBlock;

		@JsonProperty("localDisconnectReason")
		private String localDisconnectReason;

		@JsonProperty("needSyncFromPeer")
		private Boolean needSyncFromPeer;

		@JsonProperty("needSyncFromUs")
		private Boolean needSyncFromUs;

		@JsonProperty("nodeCount")
		private Long nodeCount;

		@JsonProperty("nodeId")
		private String nodeId;

		@JsonProperty("port")
		private Integer port;

		@JsonProperty("remainNum")
		private Long remainNum;

		@JsonProperty("remoteDisconnectReason")
		private String remoteDisconnectReason;

		@JsonProperty("score")
		private Long score;

		@JsonProperty("syncBlockRequestedSize")
		private BigInteger syncBlockRequestedSize;

		@JsonProperty("syncFlag")
		private Boolean syncFlag;

		@JsonProperty("syncToFetchSize")
		private BigInteger syncToFetchSize;

		@JsonProperty("syncToFetchSizePeekNum")
		private BigInteger syncToFetchSizePeekNum;

		@JsonProperty("unFetchSynNum")
		private BigInteger unFetchSynNum;

	}

}
