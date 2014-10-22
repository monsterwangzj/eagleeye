package com.chengyi.eagleeye.network.ping;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.chengyi.eagleeye.util.ApplicaRuntime;

/**
 * PING 122.11.32.129 (122.11.32.129) 56(84) bytes of data.
64 bytes from 122.11.32.129: icmp_seq=1 ttl=64 time=1.11 ms
64 bytes from 122.11.32.129: icmp_seq=2 ttl=64 time=0.174 ms
64 bytes from 122.11.32.129: icmp_seq=3 ttl=64 time=0.200 ms
64 bytes from 122.11.32.129: icmp_seq=4 ttl=64 time=0.173 ms
64 bytes from 122.11.32.129: icmp_seq=5 ttl=64 time=0.187 ms

--- 122.11.32.129 ping statistics ---
5 packets transmitted, 5 received, 0% packet loss, time 4001ms
rtt min/avg/max/mdev = 0.173/0.369/1.113/0.372 ms





Pinging 122.11.32.119 with 32 bytes of data:
Reply from 122.11.32.119: bytes=32 time=8ms TTL=60
Reply from 122.11.32.119: bytes=32 time=7ms TTL=60
Reply from 122.11.32.119: bytes=32 time=39ms TTL=60
Reply from 122.11.32.119: bytes=32 time=9ms TTL=60
Reply from 122.11.32.119: bytes=32 time=5ms TTL=60

Ping statistics for 122.11.32.119:
    Packets: Sent = 5, Received = 5, Lost = 0 (0% loss),
Approximate round trip times in milli-seconds:
    Minimum = 5ms, Maximum = 39ms, Average = 13ms


丢包率、相应时间（分最大、最小、平均）

 * 丢包率、响应时间（最大、最小、平均）
 * 
 * @author wangzhaojun
 * 
 */
public class PingUtil {
	private static final Integer pingCount = 5;

	public static PingResult doPingCmd(String destIp) {
		LineNumberReader input = null;
		String pingCmd = null;
		try {
			if (ApplicaRuntime.osName == null) {
				ApplicaRuntime.osName = System.getProperties().getProperty("os.name");
			}
			if (ApplicaRuntime.osName.startsWith("Windows")) {
				pingCmd = "cmd /c ping -n " + pingCount + " {0}";
				pingCmd = MessageFormat.format(pingCmd, destIp);
			} else if (ApplicaRuntime.osName.startsWith("Linux")) {
				pingCmd = "ping -c " + pingCount + " {0}";
				pingCmd = MessageFormat.format(pingCmd, destIp);
			} else {
				System.out.println("not support OS");
				return null;
			}

			Process process = Runtime.getRuntime().exec(pingCmd);
			InputStreamReader ir = new InputStreamReader(process.getInputStream());
			input = new LineNumberReader(ir);
			String line;
			List<String> response = new ArrayList<String>();

			while ((line = input.readLine()) != null) {
				if (!line.equals("")) {
					response.add(line + "\r\n");
				}
			}
			
			System.out.println(pingCmd);
			System.out.println(response);
			if (ApplicaRuntime.osName.startsWith("Windows")) {
				return parseWindowsMsg(response);
			} else {
				return parseLinuxMsg(response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static PingResult parseWindowsMsg(List<String> response) {
		PingResult pingResult = new PingResult();
		
		StringBuilder responsestr = new StringBuilder();
		for (String str : response) {
			responsestr.append(str);
			
			if (str.toLowerCase().contains("loss")) {
				String percent = str.substring(str.indexOf("(") + 1, str.indexOf("% loss)"));
				pingResult.setLossPercent(Float.parseFloat(percent));
			}
			if (str.toString().contains("Minimum = ")) {
				String minimum = str.substring(str.indexOf("Minimum = ") + 10, str.indexOf("ms, Maximum"));
				String maximum = str.substring(str.indexOf("Maximum = ") + 10, str.indexOf("ms, Average"));
				String average = str.substring(str.indexOf("Average = ") + 10, str.lastIndexOf("ms"));
				pingResult.setMinimum(Float.parseFloat(minimum));
				pingResult.setAverage(Float.parseFloat(average));
				pingResult.setMaximum(Float.parseFloat(maximum));
			}
		}
		
		if (pingResult.getLossPercent() == 100.) {
			pingResult.setStatus(PingResult.STATUS_TOTAL_TIMEOUT);
			if (responsestr.toString().toLowerCase().contains("unreachable")) {
				pingResult.setStatus(PingResult.STATUS_UNREACHABLE);
			}
		} else if (pingResult.getLossPercent() > 0) {
			pingResult.setStatus(PingResult.STATUS_PARTLY_TIMEOUT);
		}
		pingResult.setResponseContent(responsestr.toString());
		return pingResult;
	}

	private static PingResult parseLinuxMsg(List<String> response) {
		PingResult pingResult = new PingResult();
		StringBuilder responsestr = new StringBuilder();
		
		for (String str : response) {
			responsestr.append(str);
			if (str.toLowerCase().contains("loss")) {
				String percent = str.substring(str.indexOf("received, ") + 10, str.indexOf("% packet loss"));
				if (percent.contains("errors")) {
					percent = percent.substring(percent.indexOf("errors, ") + 7);
				}
				pingResult.setLossPercent(Float.parseFloat(percent));
			}
			if (str.toString().contains("rtt min")) {
				int index = str.indexOf("= ");
				String sub = str.substring(index + 2, str.lastIndexOf("/"));

				String[] arr = sub.split("/");
				String minimum = arr[0];
				String average = arr[1];
				String maximum = arr[2];

				pingResult.setMinimum(Float.parseFloat(minimum));
				pingResult.setAverage(Float.parseFloat(average));
				pingResult.setMaximum(Float.parseFloat(maximum));
			}
		}
		
		if (pingResult.getLossPercent() == 100.) {
			pingResult.setStatus(PingResult.STATUS_TOTAL_TIMEOUT);
			if (responsestr.toString().toLowerCase().contains("unreachable")) {
				pingResult.setStatus(PingResult.STATUS_UNREACHABLE);
			}
		} else if (pingResult.getLossPercent() > 0) {
			pingResult.setStatus(PingResult.STATUS_PARTLY_TIMEOUT);
		}
		pingResult.setResponseContent(responsestr.toString());
		
		System.out.println(pingResult);
		return pingResult;
	}

}
