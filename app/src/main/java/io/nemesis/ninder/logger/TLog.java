/*
 * Copyright (c) 2015. Tumba Solutions
 */

package io.nemesis.ninder.logger;

import android.util.Log;

import java.io.Serializable;

/**
 * Tumba Solutions Android logger
 */
public final class TLog {
	private static volatile TLogConfiguration config;

	static {
		config = new TLogConfiguration(); // default configuration
	}

	private TLog() {
	}

	public static void updateConfiguration(final TLogConfiguration configuration) {
		Validate.notNull(configuration);
		config = configuration;
	}

	public static void v(final String message) {
		TLog.v(null, message);
	}

	public static void v(final String message, final Throwable throwable) {
		TLog.v(null, message, throwable);
	}

	public static void v(final String tag, final String message) {
		TLog.v(tag, message, null);
	}

	public static void v(final String tag, final String message, final Throwable throwable) {
		TLog.logMessage(Type.VERBOSE, tag, message, throwable);
	}

	public static void d(final String message) {
		TLog.d(null, message);
	}

	public static void d(final String message, final Throwable throwable) {
		TLog.d(null, message, throwable);
	}

	public static void d(final String tag, final String message) {
		TLog.d(tag, message, null);
	}

	public static void d(final String tag, final String message, final Throwable throwable) {
		TLog.logMessage(Type.DEBUG, tag, message, throwable);
	}

	public static void i(final String message) {
		TLog.i(null, message);
	}

	public static void i(final String tag, final String message) {
		TLog.i(tag, message, null);
	}

	public static void i(final String tag, final String message, final Throwable throwable) {
		TLog.logMessage(Type.INFO, tag, message, throwable);
	}

	public static void w(final String message) {
		TLog.w(null, message);
	}

	public static void w(final String tag, final String message) {
		TLog.w(tag, message, null);
	}

	public static void w(final String message, final Throwable throwable) {
		TLog.w(null, message, throwable);
	}

	public static void w(final String tag, final String message, final Throwable throwable) {
		TLog.logMessage(Type.WARN, tag, message, throwable);
	}

	public static void e(final String message) {
		TLog.e(null, message);
	}

	public static void e(final String message, final Throwable throwable) {
		TLog.e(null, message, throwable);
	}

	public static void e(final String tag, final String message) {
		TLog.e(tag, message, null);
	}

	public static void e(final String tag, final String message, final Throwable throwable) {
		TLog.logMessage(Type.ERROR, tag, message, throwable);
	}

	private static void logMessage(final Type type, final String tag, final String message, final Throwable throwable) {
		if (!config.isEnabled()) {
			return;
		}

		Validate.notNull(type);

		if (type.getLevel() < config.getLogLevel().getLevel()) {
			return;
		}

		final StackTraceElement callerStackTraceElement = getCallerStackTraceElement();

		String logTag = getLogTag(tag, callerStackTraceElement);
		String logMessage = getLogMessage(message, callerStackTraceElement);

		if (throwable != null) {
			logMessage = String.format("%s\n%s", logMessage, Log.getStackTraceString(throwable));
		}
		config.getLoggerSink().sinkLogMessage(type.getLevel(), logTag, logMessage);
	}

	private static String getLogTag(final String tag, final StackTraceElement callerStackTraceElement) {
		String logTag = tag;

		if (config.isGenerateTag() && StringUtils.isBlank(tag)) {
			// if generation is enabled and no tag literal is passed then - generate

			if (callerStackTraceElement != null) {
				String className = callerStackTraceElement.getClassName();

				if (StringUtils.isNotBlank(className)) {
					int lastDot = className.lastIndexOf('.');
					if (!(lastDot == -1 || lastDot + 1 >= className.length())) {
						className = className.substring(++lastDot);
					}
				}

				if (StringUtils.isNotBlank(className)) {
					logTag = className;
				}
			}
		}

		if (StringUtils.isNotBlank(config.getTagPrefix())) {
			if (StringUtils.isNotBlank(logTag)) {
				logTag = String.format("%s %s", config.getTagPrefix(), logTag);
			} else {
				logTag = config.getTagPrefix();
			}
		}

		return logTag;
	}

	private static String getLogMessage(final String message, final StackTraceElement callerStackTraceElement) {
		String logMessage = message;

		if (config.isGenerateMessageInfo()) {
			String fileName = callerStackTraceElement.getFileName();
			int lineNumber = callerStackTraceElement.getLineNumber();
			String methodName = callerStackTraceElement.getMethodName();

			logMessage = String.format("%s:%d %s()", fileName, lineNumber, methodName);
			if (StringUtils.isNotBlank(message)) {
				logMessage = String.format("%s - %s", logMessage, message);
			}
		}

		return logMessage;
	}

	/**
	 * Return the {@link StackTraceElement} of the method that called {@link TLog} from the {@link Thread#currentThread()}
	 * if {@link TLogConfiguration#isGenerateTag()} or {@link TLogConfiguration#isGenerateMessageInfo()} are enabled.
	 *
	 * @return the caller {@link StackTraceElement} or <code>null</code> if generation is not enabled.
	 */
	private static StackTraceElement getCallerStackTraceElement() {
		StackTraceElement callerStackTraceElement = null;

		if (config.isGenerateTag() || config.isGenerateMessageInfo()) {
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

			boolean loggerFound = false;
			String loggerClassName = TLog.class.getName();
			for (int i = 0; i < stackTrace.length; i++) {
				StackTraceElement currStackTraceElement = stackTrace[i];

				if (!loggerFound) {
					if (loggerClassName.equals(currStackTraceElement.getClassName())) {
						loggerFound = true;
					}
				} else {
					if (!(loggerClassName.equals(currStackTraceElement.getClassName()))) {
						callerStackTraceElement = currStackTraceElement;
						break;
					}
				}
			}
		}

		return callerStackTraceElement;
	}

	public enum Type {
		VERBOSE(Log.VERBOSE, "v"),
		DEBUG(Log.DEBUG, "d"),
		INFO(Log.INFO, "i"),
		WARN(Log.WARN, "w"),
		ERROR(Log.ERROR, "e");

		private final int level;
		private final String method;

		Type(final int level, final String method) {
			this.level = level;
			this.method = method;
		}

		public int getLevel() {
			return level;
		}
	}

	public static class TLogConfiguration implements Serializable {
		private final String tagPrefix;
		private final boolean enabled;
		private final boolean generateTag;
		private final boolean generateMessageInfo;
		private final Type logLevel;
		private final transient TLogMessageSink loggerSink;

		public TLogConfiguration() {
			this(null, true, true, true, Type.VERBOSE);
		}

		public TLogConfiguration(final String tagPrefix,
								 final boolean enabled, final boolean generateTag, final boolean generateMessageInfo,
								 final Type logLevel) {

			this(tagPrefix, enabled, generateTag, generateMessageInfo, logLevel, null);
		}

		public TLogConfiguration(final String tagPrefix,
								 final boolean enabled, final boolean generateTag, final boolean generateMessageInfo,
								 final Type logLevel,
								 final TLogMessageSink loggerSink) {

			this.tagPrefix = tagPrefix;
			this.enabled = enabled;
			this.generateTag = generateTag;
			this.generateMessageInfo = generateMessageInfo;
			this.logLevel = logLevel != null ? logLevel : Type.VERBOSE;
			this.loggerSink = loggerSink != null ? loggerSink : new AndroidLoggerSink();
		}

		public String getTagPrefix() {
			return tagPrefix;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public boolean isGenerateTag() {
			return generateTag;
		}

		public boolean isGenerateMessageInfo() {
			return generateMessageInfo;
		}

		public Type getLogLevel() {
			return logLevel;
		}

		public TLogMessageSink getLoggerSink() {
			return loggerSink;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			TLogConfiguration that = (TLogConfiguration) o;

			if (enabled != that.enabled) return false;
			if (generateTag != that.generateTag) return false;
			if (generateMessageInfo != that.generateMessageInfo) return false;
			if (tagPrefix != null ? !tagPrefix.equals(that.tagPrefix) : that.tagPrefix != null) return false;
			return logLevel == that.logLevel;

		}

		@Override
		public int hashCode() {
			int result = tagPrefix != null ? tagPrefix.hashCode() : 0;
			result = 31 * result + (enabled ? 1 : 0);
			result = 31 * result + (generateTag ? 1 : 0);
			result = 31 * result + (generateMessageInfo ? 1 : 0);
			result = 31 * result + (logLevel != null ? logLevel.hashCode() : 0);
			return result;
		}

		@Override
		public String toString() {
			return "TLogConfiguration{" +
					"tagPrefix='" + tagPrefix + '\'' +
					", enabled=" + enabled +
					", generateTag=" + generateTag +
					", generateMessageInfo=" + generateMessageInfo +
					", logLevel=" + logLevel +
					'}';
		}
	}

	private static final class StringUtils {
		private StringUtils() {
		}

		public static boolean isBlank(final CharSequence cs) {
			int strLen;
			if (cs == null || (strLen = cs.length()) == 0) {
				return true;
			}
			for (int i = 0; i < strLen; i++) {
				if (!Character.isWhitespace(cs.charAt(i))) {
					return false;
				}
			}
			return true;
		}

		public static boolean isNotBlank(final CharSequence cs) {
			return !isBlank(cs);
		}
	}

	private static final class Validate {
		private Validate() {
		}

		public static <T> void notNull(final T object) {
			Validate.notNull(object, "Validation exception, object cannot be null.");
		}

		public static <T> void notNull(final T object, final String message) {
			if (object == null) {
				throw new NullPointerException(message);
			}
		}
	}

	public interface TLogMessageSink {
		void sinkLogMessage(int priority, String tag, String message);
	}

	private final static class AndroidLoggerSink implements TLogMessageSink {
		private AndroidLoggerSink() {
		}

		@Override
		public void sinkLogMessage(final int priority, final String tag, final String message) {
			Log.println(priority, tag, message);
		}
	}
}
