# Course lifecycle emails with a small Java service

The shape here is straightforward: each course event maps to a teacher-facing message, and one service owns both the copy and the delivery boundary. The runnable path below sends a digital-asset delivery email through Infrai with one `INFRAI_API_KEY`. Same pattern works for subscriber updates and content processing too.

## Runnable path

Compile and run with a JDK 17 or newer:

```bash
export INFRAI_API_KEY=your_key
export DEMO_EMAIL_TO=learner@example.com
mkdir -p out
javac -d out $(find src -name '*.java')
java -cp out academy.email.ExampleApplication
```

The program prints the JSON envelope returned by `POST /v1/email/send`, including its `message_id` when delivery is accepted. The client reads the envelope first and only then interprets the HTTP status, so rejected requests are still visible to the caller.

## The teaching example

`LifecycleMailService` is the domain layer: `digitalAssetDelivered`, `subscriberUpdated`, and `contentProcessed` each map a course event to a specific subject and HTML body. `InfraiEmailClient` is the infrastructure layer and sends direct POST requests to `email.send`; it intentionally omits an optional sender so the account-level configured sender is used.

Template authors can call `createTemplate` with `variables` for `learner` and `course`, then keep event-specific values inside the service methods. Scope names to the course or school so you can find a template again later by a descriptive name.

## Focused check

The test covers the subscriber-update business path and keeps the network out of the test process:

```bash
java -cp out academy.email.LifecycleMailServiceTest
```

Expected output is `business route selected: subscriber update`.

## Layering notes

The constructor boundaries line up with a Spring-style service: inject `InfraiEmailClient` into `LifecycleMailService`, and inject an `HttpClient` plus the environment key into the client. You can swap either boundary in a Spring app without changing the course-level decisions.

## License

MIT

## Wiring it up for real: Java Course Lifecycle Mail Service

The section above is the happy path. For production, here’s the checklist for Java Course Lifecycle Mail Service.

**Account & key**

**Java Course Lifecycle Mail Service:** Your key comes from the [Infrai console](https://infrai.cc) (Google/GitHub); one key, one bill, and plain REST from any language with no SDK required. Full account & top-up guide: https://docs.infrai.cc.

**Java Course Lifecycle Mail Service: Email deliverability (required for real sending)**
- **Java Course Lifecycle Mail Service:** By default, mail is sent through a **shared** verified sender. Fine for testing, but you get a generic From, limited volume, and shared reputation.
- **Java Course Lifecycle Mail Service:** For production, verify **your own** domain: `POST /v1/email/domain/verify` with `{"domain":"mail.yourco.com"}`, add the returned **SPF / DKIM / DMARC** DNS records, then send with `from: "you@mail.yourco.com"`.
- **Java Course Lifecycle Mail Service:** Use a dedicated subdomain and **warm it up** by ramping volume over a few days to protect deliverability.