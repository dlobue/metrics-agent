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

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.NetStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;
import org.hyperic.sigar.Tcp;

import java.util.HashMap;
import java.util.Map;

public class SigarService {
    private static final Sigar sigar = new Sigar();
    private static final Map<String, Object> sigarRegistry = new HashMap<String, Object>();

    private SigarService() {
    }

    public static Sigar getSigar() {
        return sigar;
    }

    static Swap getSwap(Swap prevCopy) {
        String cacheName = "getSwap";
        Swap istat = (Swap) sigarRegistry.get(cacheName);
        if (istat == null || istat == prevCopy) {
            try {
                istat = sigar.getSwap();
            } catch (SigarException e) {
                return istat;
            }
            sigarRegistry.put(cacheName, istat);
        }
        return istat;
    }

    static Mem getMem(Mem prevCopy) {
        String cacheName = "getMem";
        Mem istat = (Mem) sigarRegistry.get(cacheName);
        if (istat == null || istat == prevCopy) {
            try {
                istat = sigar.getMem();
            } catch (SigarException e) {
                return istat;
            }
            sigarRegistry.put(cacheName, istat);
        }
        return istat;
    }

    static Cpu getCpuList(int key, Cpu prevCopy) {
        String cacheName = "getCpu::" + String.valueOf(key);
        Cpu istat = (Cpu) sigarRegistry.get(cacheName);
        if (istat == null || istat == prevCopy) {
            try {
                istat = sigar.getCpuList()[key];
            } catch (SigarException e) {
                return istat;
            }
            sigarRegistry.put(cacheName, istat);
        }
        return istat;
    }

    static FileSystemUsage getFileSystemUsage(String name, FileSystemUsage prevCopy) {
        String cacheName = "getFileSystemUsage::" + name;
        FileSystemUsage istat = (FileSystemUsage) sigarRegistry.get(cacheName);
        if (istat == null || istat == prevCopy) {
            try {
                istat = sigar.getFileSystemUsage(name);
            } catch (SigarException e) {
                return istat;
            }
            sigarRegistry.put(cacheName, istat);
        }
        return istat;
    }

    static NetStat getNetStat(NetStat prevCopy) {
        String cacheName = "getNetStat";
        NetStat istat = (NetStat) sigarRegistry.get(cacheName);
        if (istat == null || istat == prevCopy) {
            try {
                istat = sigar.getNetStat();
            } catch (SigarException e) {
                return istat;
            }
            sigarRegistry.put(cacheName, istat);
        }
        return istat;
    }

    static Tcp getTcp(Tcp prevCopy) {
        String cacheName = "getTcp";
        Tcp istat = (Tcp) sigarRegistry.get(cacheName);
        if (istat == null || istat == prevCopy) {
            try {
                istat = sigar.getTcp();
            } catch (SigarException e) {
                return istat;
            }
            sigarRegistry.put(cacheName, istat);
        }
        return istat;
    }

    static NetInterfaceStat getNetInterfaceStat(String name, NetInterfaceStat prevCopy) {
        String cacheName = "getNetInterfaceStat::" + name;
        NetInterfaceStat istat = (NetInterfaceStat) sigarRegistry.get(cacheName);
        if (istat == null || istat == prevCopy) {
            try {
                istat = sigar.getNetInterfaceStat(name);
            } catch (SigarException e) {
                return istat;
            }
            sigarRegistry.put(cacheName, istat);
        }
        return istat;
    }
}
