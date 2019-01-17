package com.opencore.kafka;

import java.io.File;

public class CommandLine {

  public static void main(String[] args) {
    if (args.length != 3) {
      System.out.println("Usage: aclexporter import|export aclfile bootstrapservers");
      System.exit(1);
    }
    String command = args[0];
    String aclFile = args[1];
    String bootstrapServers = args[2];

    try (AclMigrationTool migrationTool = new AclMigrationTool(bootstrapServers)) {
      if (command.equals("export")) {
        migrationTool.exportAcls(new File(aclFile));
      } else if (command.equals("import")) {
        migrationTool.importAcls(new File(aclFile));
      } else {
        System.out.println("Usage: aclexporter import|export aclfile");
      }
    }
  }
}
