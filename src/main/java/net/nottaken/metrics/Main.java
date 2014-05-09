/**
 * Copyright 2014 Rackspace
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.nottaken.metrics;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.riemann.Riemann;
import com.codahale.metrics.riemann.RiemannReporter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        MetricRegistry registry = new MetricRegistry();
        registry.registerAll(new OSMetricsSet());
        registry.registerAll(new FilesystemMetricsSet());
        registry.registerAll(new NetworkMetricsSet());

        Config config = ConfigFactory.load();

        boolean reporterConfigured = false;

        long reportInterval = config.getDuration("report_interval", TimeUnit.SECONDS);

        if (config.getBoolean("enable_console_reporter")) {
            final ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .build();
            reporter.start(reportInterval, TimeUnit.SECONDS);
            reporterConfigured = true;
        }


        try {
            log.info("Attempting to configure Riemann metric reporter");
            Riemann riemann = new Riemann(config.getString("RIEMANN_HOST"),
                    config.getInt("RIEMANN_PORT"));

            RiemannReporter.Builder builder = RiemannReporter
                    .forRegistry(registry)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .convertRatesTo(TimeUnit.SECONDS);
            if (!config.getString("RIEMANN_SEPARATOR").isEmpty()) {
                builder.useSeparator(config.getString("RIEMANN_SEPARATOR"));
            }
            if (!config.getString("RIEMANN_TTL").isEmpty()) {
                builder.withTtl(((float) config.getDouble("RIEMANN_TTL")));
            }
            if (!config.getString("RIEMANN_LOCALHOST").isEmpty()) {
                builder.localHost(config.getString("RIEMANN_LOCALHOST"));
            }
            if (!config.getString("RIEMANN_PREFIX").isEmpty()) {
                builder.prefixedWith(config.getString("RIEMANN_PREFIX"));
            }
            if (!config.getString("RIEMANN_TAGS").isEmpty()) {
                builder.tags(config.getStringList("RIEMANN_TAGS"));
            }
            RiemannReporter riemannReporter = builder.build(riemann);

            riemannReporter.start(reportInterval, TimeUnit.SECONDS);
            log.info("Riemann metric reporter started.");
            reporterConfigured = true;
        } catch (ConfigException.Missing e) {
        } catch (IOException e) {
            System.exit(5);
        }

        try {
            Graphite graphite = new Graphite(new InetSocketAddress(config.getString("GRAPHITE_HOST"), config.getInt("GRAPHITE_PORT")));

            GraphiteReporter reporter = GraphiteReporter
                    .forRegistry(registry)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .prefixedWith(config.getString("GRAPHITE_PREFIX"))
                    .build(graphite);

            reporter.start(reportInterval, TimeUnit.SECONDS);
            reporterConfigured = true;
            log.info("Graphite metric reporter started.");
        } catch (ConfigException.Missing e) {
        }


        if (reporterConfigured) {
            try {
                while (true) {
                    Thread.sleep(5 * 60 * 1000);
                }
            } catch (InterruptedException e) {
            }
        } else {
            log.error("No reporters configured! Exiting");
            System.exit(1);
        }

    }
}
