package com.ecobazaar.backend;

import com.ecobazaar.backend.service.DataMigrationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Migration Runner - A simple way to run data migration from command line
 * This bypasses the REST API and runs migration directly
 */
@Component
public class MigrationRunner implements CommandLineRunner {

    private final DataMigrationService migrationService;

    public MigrationRunner(DataMigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0 && "migrate".equals(args[0])) {
            System.out.println("Starting data migration from Firestore exports...");
            try {
                String result = migrationService.migrateAllData();
                System.out.println(result);
                System.out.println("Migration completed successfully!");
            } catch (Exception e) {
                System.err.println("Migration failed: " + e.getMessage());
                e.printStackTrace();
            }
            System.exit(0);
        }
    }
}