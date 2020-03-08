package common.Auth;

import common.OperationResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class AuthOperationResult extends OperationResult {

    private final boolean passAuth;

}
