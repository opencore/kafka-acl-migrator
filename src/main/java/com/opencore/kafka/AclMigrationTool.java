package com.opencore.kafka;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateAclsResult;
import org.apache.kafka.clients.admin.DescribeAclsResult;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclBindingFilter;

public class AclMigrationTool {
  private AdminClient adminClient;
  private Gson gson = new GsonBuilder().create();

  public AclMigrationTool(Properties props) {
    adminClient = AdminClient.create(props);
  }

  public AclMigrationTool(String bootstrapServers) {
    Properties props = new Properties();
    props.setProperty("bootstrap.servers", bootstrapServers);
    adminClient = AdminClient.create(props);
  }

  public void importAcls(File aclFile) {
    try (JsonReader reader = new JsonReader(new FileReader(aclFile))) {
      AclBinding[] loadedBindings = gson.fromJson(reader, AclBinding[].class);
      CreateAclsResult result = adminClient.createAcls(Arrays.asList(loadedBindings));
      result.all().get();
      System.out.println("Imported " + loadedBindings.length + " acls!");
    } catch (FileNotFoundException e) {
      System.out.println(e.getMessage());
    } catch (IOException e) {
      System.out.println(e.getMessage());
    } catch (InterruptedException e) {
      System.out.println(e.getMessage());
    } catch (ExecutionException e) {
      System.out.println(e.getMessage());
    }
  }

  public void exportAcls(File aclFile) {
    try (Writer writer = new FileWriter(aclFile)) {
      DescribeAclsResult acls = adminClient.describeAcls(AclBindingFilter.ANY);
      Collection<AclBinding> aclBindings = acls.values().get();
      gson.toJson(aclBindings, writer);
      System.out.println("Exported " + aclBindings.size() + " acls!");
    } catch (InterruptedException e) {
      System.out.println(e.getMessage());
    } catch (ExecutionException e) {
      System.out.println(e.getMessage());
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }

  }
}
