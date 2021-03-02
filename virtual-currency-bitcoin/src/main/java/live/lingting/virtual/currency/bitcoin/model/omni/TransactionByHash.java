package live.lingting.virtual.currency.bitcoin.model.omni;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import lombok.Data;
import lombok.NoArgsConstructor;
import live.lingting.virtual.currency.core.Endpoints;
import live.lingting.virtual.currency.core.util.JacksonUtils;

/**
 * @author lingting 2020-12-14 10:13
 */
@NoArgsConstructor
@Data
public class TransactionByHash implements Domain<TransactionByHash> {

	@JsonProperty("amount")
	private BigDecimal amount;

	@JsonProperty("block")
	private BigInteger block;

	@JsonProperty("blockhash")
	private String blockHash;

	@JsonProperty("blocktime")
	private Long blockTime;

	@JsonProperty("confirmations")
	private BigInteger confirmations;

	@JsonProperty("divisible")
	private Boolean divisible;

	@JsonProperty("fee")
	private String fee;

	@JsonProperty("flags")
	private Object flags;

	@JsonProperty("ismine")
	private Boolean isMine;

	@JsonProperty("positioninblock")
	private Integer positionInBlock;

	@JsonProperty("propertyid")
	private Integer propertyId;

	@JsonProperty("propertyname")
	private String propertyName;

	@JsonProperty("referenceaddress")
	private String referenceAddress;

	@JsonProperty("sendingaddress")
	private String sendingAddress;

	@JsonProperty("txid")
	private String txId;

	@JsonProperty("type")
	private String type;

	@JsonProperty("type_int")
	private Integer typeInt;

	@JsonProperty("valid")
	private Boolean valid;

	@JsonProperty("version")
	private Integer version;

	@Override
	public TransactionByHash of(Endpoints endpoints, Object params) throws JsonProcessingException {
		HttpRequest request = HttpRequest.get(endpoints.getHttpUrl("v1/transaction/tx/" + params));
		return JacksonUtils.toObj(request.execute().body(), TransactionByHash.class);
	}

}
