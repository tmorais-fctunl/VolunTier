package voluntier.util;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import de.mkammerer.argon2.Argon2Factory.Argon2Types;

public class Argon2Util {
	private static final int ITERATIONS = 3;
	private static final int MEMORY_USAGE = 16384;
	private static final int THREADS = 1;
	private static final int SALT_LENGTH = 8;
	private static final int HASH_LENGTH = 32;
	public static Argon2 argon2 = Argon2Factory.create(Argon2Types.ARGON2id, SALT_LENGTH, HASH_LENGTH);
	
	public static void setDefaultParameters() {
		argon2 = Argon2Factory.create(Argon2Types.ARGON2id, 8, 32);
	}
	
	public static String hashPassword(String password) {
		return argon2.hash(ITERATIONS, MEMORY_USAGE, THREADS, password);
	}
	
	public static boolean verify(String hash, String password) {
		return argon2.verify(hash, password);
	}
}
