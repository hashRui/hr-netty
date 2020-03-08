package common.keepalive;

import common.OperationResult;
import lombok.Data;

@Data
public class KeepaliveOperationResult extends OperationResult {

    private final long time;

}
