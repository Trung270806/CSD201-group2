import os
from reportlab.lib.pagesizes import letter, A4
from reportlab.lib import colors
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, ListFlowable, ListItem
)

def generate_pdf():
    pdf_filename = os.path.join("latex", "report.pdf")
    doc = SimpleDocTemplate(
        pdf_filename,
        pagesize=A4,
        leftMargin=54,
        rightMargin=54,
        topMargin=54,
        bottomMargin=54
    )

    styles = getSampleStyleSheet()

    # Custom styles
    title_style = ParagraphStyle(
        'DocTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=15,
        leading=18,
        alignment=1, # Center
        spaceAfter=4
    )

    subtitle_style = ParagraphStyle(
        'DocSubTitle',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=11,
        leading=14,
        alignment=1, # Center
        spaceAfter=12
    )

    meta_style = ParagraphStyle(
        'DocMeta',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=10,
        leading=13,
        alignment=1, # Center
        spaceAfter=18
    )

    abstract_style = ParagraphStyle(
        'DocAbstract',
        parent=styles['Normal'],
        fontName='Helvetica-Oblique',
        fontSize=9.5,
        leading=13,
        leftIndent=24,
        rightIndent=24,
        alignment=4, # Justify
        spaceAfter=16
    )

    h1_style = ParagraphStyle(
        'H1',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=12,
        leading=15,
        spaceBefore=14,
        spaceAfter=6
    )

    h2_style = ParagraphStyle(
        'H2',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=10.5,
        leading=13,
        spaceBefore=10,
        spaceAfter=4
    )

    body_style = ParagraphStyle(
        'Body',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9.5,
        leading=13.5,
        spaceAfter=6,
        alignment=4 # Justify
    )

    caption_style = ParagraphStyle(
        'Caption',
        parent=styles['Normal'],
        fontName='Helvetica-Oblique',
        fontSize=8.5,
        leading=11,
        alignment=1, # Center
        spaceBefore=4,
        spaceAfter=12
    )

    story = []

    # Title & Metadata
    story.append(Paragraph("07. Bank Transaction History System", title_style))
    story.append(Paragraph("Experimental Report: Real-Time Transaction Management and Data Structure Performance Evaluation", subtitle_style))
    story.append(Paragraph("Group 07 --- CSD201<br/>July 22, 2026", meta_style))

    # Abstract
    abstract_text = (
        "<b>Abstract</b> --- This report presents empirical performance evaluation and architectural analysis for the "
        "<i>Bank Transaction History System</i> (Group 07, CSD201 Project). The system models real-time banking operations "
        "under strict compliance rules: prohibiting direct record deletion or modification (preserving auditability via "
        "<code>REVERSAL</code> transactions) and enforcing strict overdraft prevention. We experimentally investigate three core "
        "Research Questions (RQ1, RQ2, RQ3) comparing four custom-implemented data structures---Doubly Linked List, "
        "Binary Search Tree (BST), Hash Table, and Sorted Linked List---on a scale of 100,000 transaction records across 1,000 "
        "randomized queries. Experimental results demonstrate that BST pruning provides superior performance for range queries "
        "(2.84&times; faster than Doubly Linked List), binary search on linked lists fails to achieve logarithmic efficiency "
        "due to pointer traversal overhead and O(N<sup>2</sup>) insertion costs, and multi-criteria filtering easily satisfies "
        "the 200 ms response time SLA threshold (achieving &lt; 0.20 ms per query)."
    )
    story.append(Paragraph(abstract_text, abstract_style))

    # Section 1: Introduction
    story.append(Paragraph("1. Introduction", h1_style))
    intro_p1 = (
        "Modern core banking systems handle millions of daily transactions, demanding both strict auditing integrity and "
        "sub-millisecond query performance. In accordance with banking regulations, historical ledgers must be immutable: "
        "transaction deletion or modification is strictly prohibited, requiring corrective actions to be recorded as distinct "
        "<code>REVERSAL</code> transactions. Furthermore, account balances must strictly prevent unauthorized overdrafts during "
        "withdrawal operations."
    )
    story.append(Paragraph(intro_p1, body_style))

    intro_p2 = (
        "As transaction volume scales to 100,000 records and beyond, linear storage mechanisms become performance bottlenecks. "
        "This experimental study addresses three key Research Questions (RQs) to guide data structure selection for real-time "
        "transaction management:"
    )
    story.append(Paragraph(intro_p2, body_style))

    rqs = [
        "<b>Research Question 1 (RQ1):</b> Does a Doubly Linked List, Binary Search Tree (BST), or Hash Table yield the fastest execution time when querying transaction history by timestamp range over 100,000 records --- measured across 1,000 randomized range queries?",
        "<b>Research Question 2 (RQ2):</b> Is Binary Search superior to Linear Search when searching for transactions by ID on a Sorted Linked List, and does the high cost of maintaining sorted order upon new insertion negate any potential search advantage?",
        "<b>Research Question 3 (RQ3):</b> Which data structure most efficiently supports multi-criteria filtering (date range + transaction type + amount range) simultaneously while maintaining a strict response time SLA under 200 ms on a 100,000-record dataset?"
    ]
    for rq in rqs:
        story.append(Paragraph(f"• {rq}", ParagraphStyle('RQ', parent=body_style, leftIndent=12, spaceAfter=4)))

    story.append(Paragraph("To ensure stability and eliminate random execution noise, all experiments are conducted with a fixed random seed and pre-warmed JVM execution cycles.", body_style))

    # Section 2: Experimental Setup
    story.append(Paragraph("2. Experimental Setup", h1_style))
    story.append(Paragraph("The experimental environment evaluates custom Java implementations of data structures without relying on built-in Java Collection utilities (<code>java.util.LinkedList</code>, <code>java.util.HashMap</code>, etc.). The shared simulation parameters are detailed in Table 1.", body_style))

    t1_data = [
        [Paragraph("<b>Parameter</b>", body_style), Paragraph("<b>Value</b>", body_style)],
        ["Total Transaction Dataset Size (N)", "100,000 records"],
        ["Active Bank Accounts", "10,000 accounts"],
        ["Benchmark Repetitions (Q)", "1,000 randomized queries"],
        ["JVM Warmup Runs", "500 query pre-warm cycles"],
        ["Query SLA Response Threshold", "< 200 ms per query"],
        ["Audit Trail Integrity", "Immutable log (Reversal transactions only)"],
        ["Overdraft Prevention", "Strict balance validation (balance >= amount)"],
        ["Compared Data Structures", "Doubly Linked List, Hash Table, BST, Sorted List"]
    ]
    t1 = Table(t1_data, colWidths=[200, 280])
    t1.setStyle(TableStyle([
        ('LINEABOVE', (0,0), (-1,0), 1.5, colors.black),
        ('LINEBELOW', (0,0), (-1,0), 1.0, colors.black),
        ('LINEBELOW', (0,-1), (-1,-1), 1.5, colors.black),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
        ('BOTTOMPADDING', (0,0), (-1,-1), 3),
        ('TOPPADDING', (0,0), (-1,-1), 3),
    ]))
    story.append(t1)
    story.append(Paragraph("Table 1: Experimental setup and benchmarking configuration.", caption_style))

    # Section 3: RQ1
    story.append(Paragraph("3. RQ1: Timestamp Range Query Performance", h1_style))
    story.append(Paragraph("3.1 Experimental Design and Methodology", h2_style))
    story.append(Paragraph("Range queries select all transactions occurring within a specified time window [t_start, t_end]. We measure the total execution time for 1,000 range queries on a dataset of 100,000 transactions across three data structures:", body_style))
    story.append(Paragraph("• <b>Custom Doubly Linked List:</b> Traverses all N = 100,000 nodes sequentially from head to tail (O(N) lookup).", ParagraphStyle('B1', parent=body_style, leftIndent=12)))
    story.append(Paragraph("• <b>Custom Hash Table:</b> Performs a linear scan over internal bucket arrays to evaluate timestamp constraints for all entries (O(N) scan).", ParagraphStyle('B2', parent=body_style, leftIndent=12)))
    story.append(Paragraph("• <b>Custom Binary Search Tree (BST):</b> Nodes are ordered by timestamp string. Range retrieval uses subtree pruning (O(log N + k)), skipping branches whose subtrees fall entirely outside [t_start, t_end].", ParagraphStyle('B3', parent=body_style, leftIndent=12)))

    story.append(Paragraph("3.2 Results --- Range Query Performance", h2_style))
    t2_data = [
        [Paragraph("<b>Data Structure</b>", body_style), Paragraph("<b>Total Time (ns)</b>", body_style), Paragraph("<b>Avg Time / Query (&mu;s)</b>", body_style), Paragraph("<b>Speedup vs. DLL</b>", body_style)],
        ["Custom Doubly Linked List", "292,636,100", "292.64 \u03bcs", "1.00x (Baseline)"],
        ["Custom Hash Table", "212,109,500", "212.11 \u03bcs", "1.38x faster"],
        ["Custom Binary Search Tree (BST)", "103,071,100", "103.07 \u03bcs", "<b>2.84x faster</b>"]
    ]
    t2_formatted = []
    for row in t2_data:
        t2_formatted.append([Paragraph(str(cell), body_style) for cell in row])
    t2 = Table(t2_formatted, colWidths=[160, 110, 110, 100])
    t2.setStyle(TableStyle([
        ('LINEABOVE', (0,0), (-1,0), 1.5, colors.black),
        ('LINEBELOW', (0,0), (-1,0), 1.0, colors.black),
        ('LINEBELOW', (0,-1), (-1,-1), 1.5, colors.black),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
        ('BOTTOMPADDING', (0,0), (-1,-1), 3),
        ('TOPPADDING', (0,0), (-1,-1), 3),
    ]))
    story.append(t2)
    story.append(Paragraph("Table 2: RQ1 Empirical Benchmark Results (100,000 records, 1,000 range queries).", caption_style))

    story.append(Paragraph("3.3 Analysis", h2_style))
    story.append(Paragraph("As demonstrated in Table 2, the Custom BST achieves the fastest execution time, completing 1,000 range queries in 103.07 ms (103.07 &mu;s per query). This represents a <b>2.84&times; speedup</b> over the Doubly Linked List and a <b>2.06&times; speedup</b> over the Hash Table.", body_style))
    story.append(Paragraph("The mathematical basis for this performance gap lies in the algorithmic structural properties:", body_style))
    story.append(Paragraph("1. <b>Hash Table Limitations:</b> Although a Hash Table provides O(1) expected time for point lookups by transaction ID, hash functions destroy temporal ordering. Consequently, answering range queries requires iterating through all N elements across all hash buckets (O(N) complexity).", ParagraphStyle('O1', parent=body_style, leftIndent=12)))
    story.append(Paragraph("2. <b>BST Pruning Advantage:</b> Because the BST maintains key order based on transaction timestamps, range traversal prunes subtrees where node values are strictly less than t_start (ignoring left subtrees) or strictly greater than t_end (ignoring right subtrees). This restricts traversal to relevant nodes (O(log N + k)), dramatically reducing CPU comparison cycles.", ParagraphStyle('O2', parent=body_style, leftIndent=12)))

    story.append(Paragraph("3.4 Conclusion for RQ1", h2_style))
    story.append(Paragraph("For time-range transaction history queries over 100,000 records, the <b>Binary Search Tree (BST)</b> is the optimal data structure, outperforming both Hash Tables and Doubly Linked Lists due to subtree range pruning.", body_style))

    # Section 4: RQ2
    story.append(Paragraph("4. RQ2: Binary Search vs. Linear Search on Sorted Linked List", h1_style))
    story.append(Paragraph("4.1 Experimental Design and Hypotheses", h2_style))
    story.append(Paragraph("RQ2 examines whether maintaining a sorted linked list enables efficient binary search by transaction ID. We test two specific hypotheses:", body_style))
    story.append(Paragraph("• <b>Search Efficiency Hypothesis:</b> Does Binary Search achieve superior lookup times compared to Linear Search on a sequential linked list?", ParagraphStyle('H1', parent=body_style, leftIndent=12)))
    story.append(Paragraph("• <b>Insertion Cost Hypothesis:</b> Does the cumulative O(N<sup>2</sup>) cost of sorted insertion negate any search performance advantage?", ParagraphStyle('H2', parent=body_style, leftIndent=12)))
    story.append(Paragraph("To measure insertion overhead accurately without CPU freeze, sorted insertion performance is evaluated on N = 5,000 records, followed by 1,000 transaction ID lookup queries.", body_style))

    story.append(Paragraph("4.2 Results --- Sorted Linked List Performance", h2_style))
    t3_data = [
        [Paragraph("<b>Operation / Search Method</b>", body_style), Paragraph("<b>Total Execution Time (ns)</b>", body_style), Paragraph("<b>Avg Time per Op (&mu;s)</b>", body_style)],
        ["Sorted Insertion Cost (O(N<sup>2</sup>) total)", "91,120,700 ns", "18.22 \u03bcs / insert"],
        ["Linear Search by Transaction ID", "25,240,900 ns", "25.24 \u03bcs / lookup"],
        ["Binary Search on Linked List", "15,279,000 ns", "15.28 \u03bcs / lookup"]
    ]
    t3_formatted = []
    for row in t3_data:
        t3_formatted.append([Paragraph(str(cell), body_style) for cell in row])
    t3 = Table(t3_formatted, colWidths=[200, 140, 140])
    t3.setStyle(TableStyle([
        ('LINEABOVE', (0,0), (-1,0), 1.5, colors.black),
        ('LINEBELOW', (0,0), (-1,0), 1.0, colors.black),
        ('LINEBELOW', (0,-1), (-1,-1), 1.5, colors.black),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
        ('BOTTOMPADDING', (0,0), (-1,-1), 3),
        ('TOPPADDING', (0,0), (-1,-1), 3),
    ]))
    story.append(t3)
    story.append(Paragraph("Table 3: RQ2 Benchmark Results on Sorted Linked List (N = 5,000, 1,000 queries).", caption_style))

    story.append(Paragraph("4.3 Analysis", h2_style))
    story.append(Paragraph("While Binary Search appears marginally faster than Linear Search per lookup (15.28 &mu;s vs 25.24 &mu;s), the theoretical O(log N) divide-and-conquer advantage is fundamentally undermined by the linked list structure:", body_style))
    story.append(Paragraph("1. <b>Pointer Traversal Overhead:</b> Unlike contiguous arrays offering O(1) random indexing, locating the midpoint node in a linked list requires traversing N/2 pointers (O(N) node hops). As a result, Binary Search on a linked list incurs O(N) overall time complexity per search.", ParagraphStyle('P1', parent=body_style, leftIndent=12)))
    story.append(Paragraph("2. <b>Dominant Insertion Overhead:</b> Maintaining sorted order during record ingestion requires comparing new transactions against existing nodes, resulting in O(N<sup>2</sup>) cumulative insertion time (91.12 ms for 5,000 records). In dynamic banking environments with frequent real-time transactions, this massive insertion penalty completely negates the minor lookup difference.", ParagraphStyle('P2', parent=body_style, leftIndent=12)))

    story.append(Paragraph("4.4 Conclusion for RQ2", h2_style))
    story.append(Paragraph("Binary Search on a Sorted Linked List is <b>inefficient and impractical</b>. Pointer traversal prevents true O(log N) lookup scaling, and the severe O(N<sup>2</sup>) insertion overhead destroys operational feasibility. For ID lookups, a chained Hash Table (O(1) expected lookup and O(1) insertion) should be used instead.", body_style))

    # Section 5: RQ3
    story.append(Paragraph("5. RQ3: Multi-Criteria Filtering and SLA Compliance", h1_style))
    story.append(Paragraph("5.1 Experimental Design and Hypotheses", h2_style))
    story.append(Paragraph("Core banking dashboards require multi-criteria filtering where queries specify three criteria simultaneously: (1) Date interval [t_start, t_end], (2) Transaction Type (DEPOSIT or WITHDRAWAL), and (3) Amount range [amount_min, amount_max]. We evaluate whether candidate data structures can process multi-criteria queries across 100,000 records while maintaining a strict Service Level Agreement (SLA) threshold of <b>&lt; 200 ms per query</b>.", body_style))

    story.append(Paragraph("5.2 Results --- Multi-Criteria Benchmark and SLA Verification", h2_style))
    t4_data = [
        [Paragraph("<b>Data Structure</b>", body_style), Paragraph("<b>Total Time (ns)</b>", body_style), Paragraph("<b>Avg Time / Query (ms)</b>", body_style), Paragraph("<b>SLA Compliance (&lt; 200 ms)</b>", body_style)],
        ["Custom Doubly Linked List", "173,050,600", "0.173 ms", "<font color='green'><b>[OK] COMPLIANT</b></font>"],
        ["Custom Hash Table", "202,443,300", "0.202 ms", "<font color='green'><b>[OK] COMPLIANT</b></font>"],
        ["Custom BST (Pruning + Filtering)", "123,140,200", "<b>0.123 ms</b>", "<font color='green'><b>[OK] COMPLIANT</b></font>"]
    ]
    t4_formatted = []
    for row in t4_data:
        t4_formatted.append([Paragraph(str(cell), body_style) for cell in row])
    t4 = Table(t4_formatted, colWidths=[160, 100, 110, 110])
    t4.setStyle(TableStyle([
        ('LINEABOVE', (0,0), (-1,0), 1.5, colors.black),
        ('LINEBELOW', (0,0), (-1,0), 1.0, colors.black),
        ('LINEBELOW', (0,-1), (-1,-1), 1.5, colors.black),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
        ('BOTTOMPADDING', (0,0), (-1,-1), 3),
        ('TOPPADDING', (0,0), (-1,-1), 3),
    ]))
    story.append(t4)
    story.append(Paragraph("Table 4: RQ3 Multi-Criteria Filtering Benchmark and 200 ms SLA Verification (N = 100,000, 1,000 queries).", caption_style))

    story.append(Paragraph("5.3 Analysis", h2_style))
    story.append(Paragraph("All three data structures comfortably pass the 200 ms SLA requirement by a substantial margin:", body_style))
    story.append(Paragraph("• <b>Custom BST Performance:</b> Achieves the fastest filtering time at <b>0.123 ms per query</b> (123.14 ms total for 1,000 queries). The BST leverages temporal pruning to first isolate a small candidate subset of matching dates, then evaluates Type and Amount bounds only on that filtered subset.", ParagraphStyle('S1', parent=body_style, leftIndent=12)))
    story.append(Paragraph("• <b>Doubly Linked List &amp; Hash Table:</b> Achieve 0.173 ms and 0.202 ms per query respectively. Even under full linear scans over 100,000 records, modern in-memory RAM execution speeds keep latency well below 1 ms.", ParagraphStyle('S2', parent=body_style, leftIndent=12)))
    story.append(Paragraph("• <b>SLA Margin:</b> The worst average query latency (0.202 ms) is nearly <b>1,000&times; faster</b> than the 200 ms threshold, confirming robust system performance under heavy query workloads.", ParagraphStyle('S3', parent=body_style, leftIndent=12)))

    story.append(Paragraph("5.4 Conclusion for RQ3", h2_style))
    story.append(Paragraph("The <b>Custom BST</b> is the most effective data structure for multi-criteria transaction filtering. All evaluated structures strictly comply with the 200 ms SLA requirement for 100,000 records.", body_style))

    # Section 6: Overall Conclusions
    story.append(Paragraph("6. Overall Conclusions", h1_style))
    story.append(Paragraph("1. <b>Range Queries (RQ1):</b> The <b>Binary Search Tree (BST)</b> is the optimal structure for timestamp range searches (2.84&times; faster than Doubly Linked List), as key ordering enables efficient subtree pruning.", ParagraphStyle('C1', parent=body_style, leftIndent=12, spaceAfter=4)))
    story.append(Paragraph("2. <b>Sorted Linked List Lookups (RQ2):</b> Binary search on linked lists fails to achieve logarithmic performance due to sequential pointer traversals. The O(N<sup>2</sup>) insertion cost further renders sorted linked lists impractical for real-time transaction ingestion.", ParagraphStyle('C2', parent=body_style, leftIndent=12, spaceAfter=4)))
    story.append(Paragraph("3. <b>Multi-Criteria Filtering &amp; SLA (RQ3):</b> Combining BST date pruning with secondary criteria filtering yields the lowest latency (0.123 ms per query). All structures successfully satisfy the 200 ms SLA response threshold on 100,000 transaction records.", ParagraphStyle('C3', parent=body_style, leftIndent=12, spaceAfter=4)))

    doc.build(story)
    print(f"SUCCESS: {pdf_filename} created successfully!")

if __name__ == "__main__":
    generate_pdf()
