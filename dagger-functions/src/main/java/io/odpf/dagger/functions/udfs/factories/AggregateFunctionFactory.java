package io.odpf.dagger.functions.udfs.factories;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.functions.AggregateFunction;
import org.apache.flink.table.functions.UserDefinedFunction;

import io.odpf.dagger.common.contracts.UDFFactory;
import io.odpf.dagger.functions.udfs.aggregate.DistinctCount;
import io.odpf.dagger.functions.udfs.telemetry.AggregatedUDFTelemetryPublisher;

import java.util.HashMap;
import java.util.Map;

public class AggregateFunctionFactory implements UDFFactory {
    private Configuration daggerConfig;
    private StreamTableEnvironment streamTableEnvironment;

    public AggregateFunctionFactory(Configuration daggerConfig, StreamTableEnvironment streamTableEnvironment) {
        this.daggerConfig = daggerConfig;
        this.streamTableEnvironment = streamTableEnvironment;
    }

    @Override
    public void registerFunctions() {
        Map<String, UserDefinedFunction> aggregateFunctionsMap = addfunctions();
        // TODO : Validate if simple telemetry subsriber will work here.
        AggregatedUDFTelemetryPublisher aggregatedUDFTelemetryPublisher = new AggregatedUDFTelemetryPublisher(daggerConfig, aggregateFunctionsMap);
        aggregatedUDFTelemetryPublisher.notifySubscriber();
        aggregateFunctionsMap.forEach((aggregateFunctionName, aggregateUDF) -> streamTableEnvironment
                .registerFunction(aggregateFunctionName, (AggregateFunction) aggregateUDF));
    }

    @Override
    public Map<String, UserDefinedFunction> addfunctions() {
        HashMap<String, UserDefinedFunction> aggregateFiunctionMap = new HashMap();
        aggregateFiunctionMap.put("DistinctCount", new DistinctCount());
        return aggregateFiunctionMap;
    }
}
