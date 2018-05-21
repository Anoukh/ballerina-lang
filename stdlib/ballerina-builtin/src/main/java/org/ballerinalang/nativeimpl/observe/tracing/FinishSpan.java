/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.nativeimpl.observe.tracing;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.util.observability.ObserverContext;

import static org.ballerinalang.util.tracer.TraceConstants.NATIVE_SPAN_DATA;

/**
 * This function which implements the finishSpan method for tracing.
 */
@BallerinaFunction(
        orgName = "ballerina",
        packageName = "observe",
        functionName = "finish",
        receiver = @Receiver(type = TypeKind.STRUCT, structType = "Span", structPackage = "ballerina.observe"),
        isPublic = true
)
public class FinishSpan extends BlockingNativeCallableUnit {

    @Override
    public void execute(Context context) {
        BStruct span = (BStruct) context.getRefArgument(0);
        boolean isFinished = span.getBooleanField(0) == 1;

        if (isFinished) {
            context.setReturnValues(Utils.createErrorStruct(context, "Can not finish already finished span"));
        } else {
            span.setBooleanField(0, 1);
            ObserverContext observeContext = (ObserverContext) span.getNativeData(NATIVE_SPAN_DATA);
            OpenTracerBallerinaWrapper.getInstance()
                    .finishSpan(observeContext, span.getBooleanField(1) == 1, context);
        }
    }
}
