<pre>
  
graph TD
    subgraph Telegram
        TG[Telegram API]
    end

    subgraph Gateway_Layer
        Disp[Dispatcher Service]
    end

    subgraph Message_Broker
        Rabbit[(RabbitMQ)]
    end

    subgraph Microservices
        FileS[File Service]
        NotifS[Notification Service]
        UserS[User Service]
    end

    subgraph Business_Logic_Layer
        Node[Node Service]
        Redis[(Redis Cache)]
    end

    subgraph Storage_Layer
        DB_U[(PostgreSQL User)]
        DB_F[(PostgreSQL File)]
        S3[(MinIO Object Storage)]
        Mail[SMTP Server]
    end

    %% Flow
    TG -- Webhook --> Disp
    Disp -- Publish DTO --> Rabbit
    Rabbit -- Consume --> Node
    
    Node -- Check/Update --> Redis
    UserS -- Persist --> DB_U
    
    Node -- Command/File --> Rabbit
    Rabbit -- Auth Email --> NotifS
    NotifS -- Send --> Mail
    
    Node -- gRPC --> UserS
    Rabbit -- Save File --> FileS
    FileS -- Metadata --> DB_F
    FileS -- Binary --> S3
    
    FileS -- Result --> Rabbit
    NotifS -- Confirmed --> Rabbit
    Rabbit -- Response --> Node
    Node -- Final UI Message --> Rabbit
    Rabbit -- Outbound --> Disp
    Disp -- API Response --> TG
</pre>
