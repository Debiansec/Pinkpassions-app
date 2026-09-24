package com.example.data

import com.example.model.*

object MockDataProvider {

    val PROVINCES = listOf(
        "Gauteng",
        "Western Cape",
        "KwaZulu-Natal",
        "Eastern Cape",
        "Free State",
        "Limpopo",
        "Mpumalanga",
        "North West",
        "Northern Cape"
    )

    val CITIES_BY_PROVINCE = mapOf(
        "Gauteng" to listOf("Johannesburg", "Pretoria", "Sandton", "Midrand", "Centurion", "Rosebank", "Bedfordview"),
        "Western Cape" to listOf("Cape Town", "Camps Bay", "Stellenbosch", "Sea Point", "Century City", "Somerset West"),
        "KwaZulu-Natal" to listOf("Durban", "Umhlanga", "Ballito", "Pietermaritzburg", "Morningside"),
        "Eastern Cape" to listOf("Gqeberha (PE)", "East London", "Jeffreys Bay"),
        "Free State" to listOf("Bloemfontein", "Welkom"),
        "Limpopo" to listOf("Polokwane", "Tzaneen"),
        "Mpumalanga" to listOf("Mbombela (Nelspruit)", "Witbank"),
        "North West" to listOf("Rustenburg", "Potchefstroom"),
        "Northern Cape" to listOf("Kimberley", "Upington")
    )

    fun getInitialProfiles(): List<Profile> {
        return listOf(
            Profile(
                id = "p1",
                userId = "u1",
                displayName = "Chantelle V.",
                age = 24,
                province = "Gauteng",
                city = "Sandton",
                area = "Sandton CBD",
                category = ProfileCategory.VIP,
                description = "Sophisticated, bilingual elegance. Available for luxury dinner dates, international travel companionship, high-end corporate galas, and discreet private engagements in Sandton and Rosebank.",
                rating = 4.98f,
                reviewCount = 38,
                verifiedLevel = VerificationLevel.FULL,
                membershipTier = MembershipTier.VIP,
                isGold = true,
                isFeatured = true,
                isOnline = true,
                lastActive = "Active now",
                priceText = "R1,500 / hr",
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
                services = "VIP Dinner Dates, Travel Companion, Social Functions, Private Intimacy",
                viewsCount = 3420,
                contactClicks = 490
            ),
            Profile(
                id = "p2",
                userId = "u2",
                displayName = "Natasha K.",
                age = 26,
                province = "Western Cape",
                city = "Cape Town",
                area = "Camps Bay",
                category = ProfileCategory.EXECUTIVE,
                description = "Warm, charismatic model with a passion for fine wine and Atlantic sunsets. Providing upmarket companionship for distinguished gentlemen seeking an unforgettable experience.",
                rating = 4.95f,
                reviewCount = 29,
                verifiedLevel = VerificationLevel.PHOTO,
                membershipTier = MembershipTier.VIP,
                isGold = true,
                isFeatured = true,
                isOnline = true,
                lastActive = "Active now",
                priceText = "R2,000 / hr",
                avatarUrl = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=500&auto=format&fit=crop&q=80",
                services = "Yacht Escort, Luxury Outings, Weekend Getaways, Discreet Encounters",
                viewsCount = 2890,
                contactClicks = 380
            ),
            Profile(
                id = "p3",
                userId = "u3",
                displayName = "Zara Rose",
                age = 22,
                province = "KwaZulu-Natal",
                city = "Durban",
                area = "Umhlanga Ridge",
                category = ProfileCategory.INDEPENDENT,
                description = "Vibrant, sensual & attentive. Whether you seek a charming date for an upscale dinner in Umhlanga or a deeply relaxing massage session, I ensure unmatched discreet warmth.",
                rating = 4.92f,
                reviewCount = 22,
                verifiedLevel = VerificationLevel.PHOTO,
                membershipTier = MembershipTier.FEATURED,
                isGold = false,
                isFeatured = true,
                isOnline = false,
                lastActive = "15m ago",
                priceText = "R1,200 / hr",
                avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=500&auto=format&fit=crop&q=80",
                services = "Sensual Bodywork, Dinner Companionship, Overnight Sessions",
                viewsCount = 1950,
                contactClicks = 210
            ),
            Profile(
                id = "p4",
                userId = "u4",
                displayName = "Mia Swanepoel",
                age = 25,
                province = "Gauteng",
                city = "Pretoria",
                area = "Menlyn Maine",
                category = ProfileCategory.MASSAGE,
                description = "Certified holistic and sensual tantric massage therapist. Offering a tranquil haven of relaxation, aromatic oils, soft candlelit ambience, and full-body revitalisation.",
                rating = 4.97f,
                reviewCount = 45,
                verifiedLevel = VerificationLevel.FULL,
                membershipTier = MembershipTier.GOLD,
                isGold = true,
                isFeatured = true,
                isOnline = true,
                lastActive = "Active now",
                priceText = "R950 / session",
                avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=80",
                services = "Swedish Massage, Tantric Rituals, Nuru Body Slide, 4-Hands Spa",
                viewsCount = 4100,
                contactClicks = 612
            ),
            Profile(
                id = "p5",
                userId = "u5",
                displayName = "Sasha & Lexi",
                age = 23,
                province = "Gauteng",
                city = "Johannesburg",
                area = "Rosebank",
                category = ProfileCategory.DANCERS,
                description = "Dynamic exotic dance duo available for VIP bachelor parties, private club performances, high-energy entertainment, and duo companion bookings.",
                rating = 4.88f,
                reviewCount = 19,
                verifiedLevel = VerificationLevel.PHOTO,
                membershipTier = MembershipTier.FEATURED,
                isGold = false,
                isFeatured = true,
                isOnline = true,
                lastActive = "Active now",
                priceText = "R2,500 / hr",
                avatarUrl = "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?w=500&auto=format&fit=crop&q=80",
                services = "Duo Booking, Exotic Dance, Private Party Entertaining, Striptease",
                viewsCount = 3150,
                contactClicks = 420
            ),
            Profile(
                id = "p6",
                userId = "u6",
                displayName = "Kylie St. Claire",
                age = 27,
                province = "Western Cape",
                city = "Cape Town",
                area = "V&A Waterfront",
                category = ProfileCategory.EXECUTIVE,
                description = "Classy, intelligent, and enchanting. Well-travelled companion for elite gentlemen visiting the Mother City. 100% genuine photos, incall luxury apartment or outcall to 5-star hotels.",
                rating = 4.99f,
                reviewCount = 52,
                verifiedLevel = VerificationLevel.FULL,
                membershipTier = MembershipTier.VIP,
                isGold = true,
                isFeatured = true,
                isOnline = false,
                lastActive = "1 hr ago",
                priceText = "R2,200 / hr",
                avatarUrl = "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?w=500&auto=format&fit=crop&q=80",
                services = "5-Star Hotel Outcalls, Fine Dining, Luxury Escort, Overnight Excursions",
                viewsCount = 5200,
                contactClicks = 820
            ),
            Profile(
                id = "p7",
                userId = "u7",
                displayName = "Jessica Leigh",
                age = 21,
                province = "Gauteng",
                city = "Johannesburg",
                area = "Fourways",
                category = ProfileCategory.INDEPENDENT,
                description = "Sweet, adventurous petite beauty. Open-minded and easy to converse with. Available for stress relief sessions, private dates, and memorable weekends.",
                rating = 4.75f,
                reviewCount = 14,
                verifiedLevel = VerificationLevel.BASIC,
                membershipTier = MembershipTier.FREE,
                isGold = false,
                isFeatured = false,
                isOnline = true,
                lastActive = "Active now",
                priceText = "R800 / hr",
                avatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=500&auto=format&fit=crop&q=80",
                services = "Casual Dates, Stress Relief, Companionship",
                viewsCount = 1420,
                contactClicks = 160
            ),
            Profile(
                id = "p8",
                userId = "u8",
                displayName = "Elena Dubois",
                age = 28,
                province = "Gauteng",
                city = "Pretoria",
                area = "Brooklyn",
                category = ProfileCategory.VIP,
                description = "European sophistication in the heart of Pretoria. Tall, elegant, and discreet. For discerning professionals who appreciate refined beauty and intellectual chemistry.",
                rating = 4.96f,
                reviewCount = 31,
                verifiedLevel = VerificationLevel.FULL,
                membershipTier = MembershipTier.VIP,
                isGold = true,
                isFeatured = false,
                isOnline = false,
                lastActive = "30m ago",
                priceText = "R1,800 / hr",
                avatarUrl = "https://images.unsplash.com/photo-1519699047748-de8e457a634e?w=500&auto=format&fit=crop&q=80",
                services = "Executive Dates, Theatre Companion, Extended Travel",
                viewsCount = 2940,
                contactClicks = 390
            ),
            Profile(
                id = "p9",
                userId = "u9",
                displayName = "Chloe Dlamini",
                age = 23,
                province = "KwaZulu-Natal",
                city = "Durban",
                area = "Morningside",
                category = ProfileCategory.MASSAGE,
                description = "Warm hands and gentle soul. Specialising in deep tissue relaxation, Thai herbal compress therapy, and soothing aroma bodywork in a discreet private studio.",
                rating = 4.89f,
                reviewCount = 24,
                verifiedLevel = VerificationLevel.PHOTO,
                membershipTier = MembershipTier.FEATURED,
                isGold = false,
                isFeatured = false,
                isOnline = true,
                lastActive = "Active now",
                priceText = "R750 / hr",
                avatarUrl = "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?w=500&auto=format&fit=crop&q=80",
                services = "Deep Tissue Massage, Aromatherapy, Relaxation Therapy",
                viewsCount = 1800,
                contactClicks = 210
            ),
            Profile(
                id = "p10",
                userId = "u10",
                displayName = "Amber Fox",
                age = 24,
                province = "Western Cape",
                city = "Cape Town",
                area = "Sea Point",
                category = ProfileCategory.DANCERS,
                description = "Feisty redhead dancer and pole artist. Book me for private bachelor performances, themed photo shoots, or exclusive VIP nightlife accompaniment.",
                rating = 4.91f,
                reviewCount = 17,
                verifiedLevel = VerificationLevel.PHOTO,
                membershipTier = MembershipTier.FEATURED,
                isGold = false,
                isFeatured = false,
                isOnline = true,
                lastActive = "Active now",
                priceText = "R1,400 / hr",
                avatarUrl = "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=500&auto=format&fit=crop&q=80",
                services = "Private Pole Dancing, Club Accompaniment, Bachelor Events",
                viewsCount = 2210,
                contactClicks = 280
            ),
            Profile(
                id = "p11",
                userId = "u11",
                displayName = "Simone & Bella",
                age = 25,
                province = "Gauteng",
                city = "Sandton",
                area = "Bryanston",
                category = ProfileCategory.VIP,
                description = "High-class duo companion experience. Double the beauty, elegance, and captivating fun. Inquiries welcome from verified upscale gentlemen.",
                rating = 4.98f,
                reviewCount = 28,
                verifiedLevel = VerificationLevel.FULL,
                membershipTier = MembershipTier.GOLD,
                isGold = true,
                isFeatured = true,
                isOnline = false,
                lastActive = "2 hrs ago",
                priceText = "R3,200 / hr",
                avatarUrl = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=500&auto=format&fit=crop&q=80",
                services = "Duo Luxury Escort, Penthouse In-calls, VIP Weekend Excursions",
                viewsCount = 4700,
                contactClicks = 640
            ),
            Profile(
                id = "p12",
                userId = "u12",
                displayName = "Zandile M.",
                age = 26,
                province = "Gauteng",
                city = "Johannesburg",
                area = "Bedfordview",
                category = ProfileCategory.EXECUTIVE,
                description = "Gorgeous, educated and naturally voluptuous. Passionate about arts, cuisine, and creating intimate, peaceful moments free from every day stress.",
                rating = 4.93f,
                reviewCount = 33,
                verifiedLevel = VerificationLevel.PHOTO,
                membershipTier = MembershipTier.VIP,
                isGold = true,
                isFeatured = false,
                isOnline = true,
                lastActive = "Active now",
                priceText = "R1,600 / hr",
                avatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=500&auto=format&fit=crop&q=80",
                services = "Executive Outcalls, Romantic Dinners, Sensual Relaxation",
                viewsCount = 3100,
                contactClicks = 410
            )
        )
    }

    fun getInitialBusinesses(): List<Business> {
        return listOf(
            Business(
                id = "b1",
                ownerId = "biz_owner_1",
                name = "The Velvet Lounge & Gentlemen's Club",
                category = "Clubs & Venues",
                description = "Johannesburg's premier adult entertainment venue. Featuring world-class stage performers, private VIP champagne suites, cigar lounge, and fine dining.",
                address = "142 Rivonia Rd, Sandton, Johannesburg",
                province = "Gauteng",
                city = "Sandton",
                latitude = -26.1076,
                longitude = 28.0567,
                phone = "+27 11 784 1000",
                website = "https://pinkpassion.co.za/velvet-lounge",
                openingHours = "Tue - Sun: 19:00 - 05:00",
                rating = 4.9f,
                reviewCount = 84,
                isVip = true,
                specials = "Complimentary bottle of Veuve Clicquot with any VIP Suite booking",
                imageUrl = "https://images.unsplash.com/photo-1566417713940-fe7c737a9ef2?w=600&auto=format&fit=crop&q=80"
            ),
            Business(
                id = "b2",
                ownerId = "biz_owner_2",
                name = "Siren's Cove Luxury Spa & Sanctuary",
                category = "Massage & Wellness",
                description = "An exclusive discreet sanctuary offering 5-star hydrotherapy, 4-hands sensual massages, and private jacuzzis with ocean views.",
                address = "68 Victoria Rd, Camps Bay, Cape Town",
                province = "Western Cape",
                city = "Cape Town",
                latitude = -33.9510,
                longitude = 18.3780,
                phone = "+27 21 438 2200",
                website = "https://pinkpassion.co.za/sirens-cove",
                openingHours = "Daily: 10:00 - 23:00",
                rating = 4.95f,
                reviewCount = 67,
                isVip = true,
                specials = "Couple's Tantric Escape package at 20% discount on weekdays",
                imageUrl = "https://images.unsplash.com/photo-1540555700478-4be289fbecef?w=600&auto=format&fit=crop&q=80"
            ),
            Business(
                id = "b3",
                ownerId = "biz_owner_3",
                name = "Eclipse Gentlemen's Lounge",
                category = "Gentlemen's Club",
                description = "Durban's vibrant nightlife destination with sultry stage performers, premium cocktail bar, and private booth hospitality.",
                address = "12 Lagoon Dr, Umhlanga Rocks, Durban",
                province = "KwaZulu-Natal",
                city = "Durban",
                latitude = -29.7289,
                longitude = 31.0858,
                phone = "+27 31 561 8800",
                website = "https://pinkpassion.co.za/eclipse-lounge",
                openingHours = "Wed - Sun: 20:00 - 04:30",
                rating = 4.8f,
                reviewCount = 53,
                isVip = false,
                specials = "Happy hour 20:00 - 22:00 with half price cocktails",
                imageUrl = "https://images.unsplash.com/photo-1572116469696-31de0f17cc34?w=600&auto=format&fit=crop&q=80"
            ),
            Business(
                id = "b4",
                ownerId = "biz_owner_4",
                name = "Sensations Adult Boutique & Intimate Fashion",
                category = "Adult Boutique",
                description = "Luxury adult lifestyle store offering designer French lingerie, premium intimacy devices, massage candles, and discreet in-store consultations.",
                address = "Sandton City Mall, Sandton",
                province = "Gauteng",
                city = "Sandton",
                latitude = -26.1090,
                longitude = 28.0530,
                phone = "+27 11 883 4000",
                website = "https://pinkpassions.co.za/sensations-boutique",
                openingHours = "Mon - Sat: 09:00 - 20:00",
                rating = 4.92f,
                reviewCount = 98,
                isVip = true,
                specials = "Free discreet gift bag on all purchases over R500",
                imageUrl = "https://images.unsplash.com/photo-1558769132-cb1aea458c5e?w=600&auto=format&fit=crop&q=80"
            ),
            Business(
                id = "b5",
                ownerId = "biz_owner_5",
                name = "Aphrodite Sensual Studio & Tantra Spa",
                category = "Sensual Massage & Spa",
                description = "Private candlelit oasis specializing in 4-hand Nurui massages, warm aromatic coconut oil therapy, and full body tantric stress relief.",
                address = "Menlyn Maine Central, Pretoria",
                province = "Gauteng",
                city = "Pretoria",
                latitude = -25.7870,
                longitude = 28.2780,
                phone = "+27 12 348 9000",
                website = "https://pinkpassions.co.za/aphrodite-spa",
                openingHours = "Daily: 09:00 - 23:00",
                rating = 4.96f,
                reviewCount = 112,
                isVip = true,
                specials = "Complimentary head & shoulder massage with any 90-min booking",
                imageUrl = "https://images.unsplash.com/photo-1544161515-4ab6ce6db874?w=600&auto=format&fit=crop&q=80"
            ),
            Business(
                id = "b6",
                ownerId = "biz_owner_6",
                name = "Gold Standard Elite Escort Agency",
                category = "Escort Agency",
                description = "Cape Town's most trusted executive agency. Curating verified, multilingual companions for high-profile business clients and 5-star hotel visits.",
                address = "Victoria & Alfred Waterfront, Cape Town",
                province = "Western Cape",
                city = "Cape Town",
                latitude = -33.9050,
                longitude = 18.4200,
                phone = "+27 21 419 8000",
                website = "https://pinkpassions.co.za/gold-standard",
                openingHours = "24/7 Concierge Hotline",
                rating = 4.98f,
                reviewCount = 145,
                isVip = true,
                specials = "Chauffeured luxury transport included for VIP outcall bookings",
                imageUrl = "https://images.unsplash.com/photo-1517457373958-b7bdd4587205?w=600&auto=format&fit=crop&q=80"
            ),
            Business(
                id = "b7",
                ownerId = "biz_owner_7",
                name = "L'Amour Private Suites & Jacuzzi Club",
                category = "VIP Lounge",
                description = "Ultra-discreet private entertainment suites featuring themed rooms, heated jacuzzis, premium sound systems, and complimentary champagne service.",
                address = "Oxford Rd, Rosebank, Johannesburg",
                province = "Gauteng",
                city = "Johannesburg",
                latitude = -26.1450,
                longitude = 28.0440,
                phone = "+27 11 447 5000",
                website = "https://pinkpassions.co.za/lamour-suites",
                openingHours = "Daily: 14:00 - 06:00",
                rating = 4.88f,
                reviewCount = 64,
                isVip = false,
                specials = "Weekday 2-hour suite rental package with free sparkling wine",
                imageUrl = "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=600&auto=format&fit=crop&q=80"
            ),
            Business(
                id = "b8",
                ownerId = "biz_owner_8",
                name = "Zenith Fetish, Kink & BDSM Sanctuary",
                category = "Fetish & BDSM",
                description = "South Africa's fully-equipped dungeon and play space. Safe, sane, and consensual environment with cages, crosses, suspension rigs, and leather suites.",
                address = "Maboneng Precinct, Johannesburg",
                province = "Gauteng",
                city = "Johannesburg",
                latitude = -26.2050,
                longitude = 28.0600,
                phone = "+27 11 614 2000",
                website = "https://pinkpassions.co.za/zenith-sanctuary",
                openingHours = "Thu - Sun: 18:00 - 03:00",
                rating = 4.9f,
                reviewCount = 47,
                isVip = true,
                specials = "Introductory dominant & submissive workshop every first Thursday",
                imageUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80"
            )
        )
    }

    fun getInitialEvents(): List<Event> {
        return listOf(
            Event(
                id = "e1",
                businessId = "b1",
                title = "Pink Passions Gala Night: Masquerade Ball",
                description = "An ultra-exclusive evening of mystery, glamour, and allure. Featuring international pole artists, champagne towers, live jazz, and private lounge experiences.",
                date = "Friday, 18 September 2026",
                time = "20:00 - Late",
                location = "Velvet Lounge, Sandton",
                city = "Sandton",
                province = "Gauteng",
                ticketPriceZar = 450,
                isFeatured = true,
                imageUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80"
            ),
            Event(
                id = "e2",
                businessId = "b2",
                title = "Sunset Champagne & Siren Serenade",
                description = "Live acoustic entertainment, private terrace ocean views, and introductory sensual wellness workshops in Camps Bay.",
                date = "Saturday, 26 September 2026",
                time = "17:00 - 23:00",
                location = "Siren's Cove Sanctuary, Camps Bay",
                city = "Cape Town",
                province = "Western Cape",
                ticketPriceZar = 350,
                isFeatured = true,
                imageUrl = "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=600&auto=format&fit=crop&q=80"
            )
        )
    }

    fun getInitialProducts(): List<Product> {
        return listOf(
            Product(
                id = "prd1",
                name = "Midnight Silk & Lace Robe Set",
                description = "Handcrafted sheer black satin with hot pink lace embroidery and matching belt. Breathable luxury feel.",
                priceZar = 890,
                category = "Intimate Apparel",
                inventory = 15,
                rating = 4.9f,
                imageUrl = "https://images.unsplash.com/photo-1583846783214-7229a91b20ed?w=500&auto=format&fit=crop&q=80"
            ),
            Product(
                id = "prd2",
                name = "Aura Dual Stimulation Pleasure Device",
                description = "Whisper-quiet, medical-grade body-safe silicone with 10 pulsation frequencies, waterproof design, and magnetic USB charging.",
                priceZar = 1450,
                category = "Adult Wellness",
                inventory = 28,
                rating = 5.0f,
                imageUrl = "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=500&auto=format&fit=crop&q=80"
            ),
            Product(
                id = "prd3",
                name = "Sensual Warm Vanilla Massage Candle",
                description = "Organic soy wax enriched with shea butter and jojoba oil. Melts into a warm, deeply hydrating massage oil.",
                priceZar = 320,
                category = "Personal Care",
                inventory = 40,
                rating = 4.8f,
                imageUrl = "https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?w=500&auto=format&fit=crop&q=80"
            ),
            Product(
                id = "prd4",
                name = "Velvet Touch Restraints & Blindfold Kit",
                description = "Plush padded velvet handcuffs and matching soft satin blindfold for elegant sensory exploration.",
                priceZar = 550,
                category = "Accessories",
                inventory = 19,
                rating = 4.9f,
                imageUrl = "https://images.unsplash.com/photo-1512496015851-a90fb38ba796?w=500&auto=format&fit=crop&q=80"
            )
        )
    }

    fun getInitialConversations(): List<Conversation> {
        return listOf(
            Conversation(
                id = "c1",
                recipientId = "p1",
                recipientName = "Chantelle V.",
                recipientAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
                lastMessage = "Hi! I'm available this Friday evening in Sandton. Would you like to reserve dinner?",
                lastMessageTime = "10:45 AM",
                unreadCount = 2,
                isOnline = true,
                verifiedLevel = VerificationLevel.FULL
            ),
            Conversation(
                id = "c2",
                recipientId = "p2",
                recipientName = "Natasha K.",
                recipientAvatar = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=500&auto=format&fit=crop&q=80",
                lastMessage = "Thank you for reaching out! Looking forward to meeting in Camps Bay.",
                lastMessageTime = "Yesterday",
                unreadCount = 0,
                isOnline = true,
                verifiedLevel = VerificationLevel.PHOTO
            ),
            Conversation(
                id = "c3",
                recipientId = "p4",
                recipientName = "Mia Swanepoel",
                recipientAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=80",
                lastMessage = "Your 90-minute holistic massage session has been scheduled.",
                lastMessageTime = "2 days ago",
                unreadCount = 0,
                isOnline = false,
                verifiedLevel = VerificationLevel.FULL
            )
        )
    }

    fun getInitialMessages(convoId: String): List<Message> {
        return when (convoId) {
            "c1" -> listOf(
                Message("m1", "c1", "me", "You", "Good morning Chantelle, are you available for a dinner engagement in Sandton this weekend?", "10:30 AM", true),
                Message("m2", "c1", "p1", "Chantelle V.", "Hello! Yes, absolutely. I would love to accompany you. What time are you thinking?", "10:40 AM", false),
                Message("m3", "c1", "p1", "Chantelle V.", "Hi! I'm available this Friday evening in Sandton. Would you like to reserve dinner?", "10:45 AM", false)
            )
            else -> listOf(
                Message("m10", convoId, "other", "Member", "Hello! Welcome to Pink Passions. How can I assist your booking?", "Yesterday", false)
            )
        }
    }

    fun getInitialCampaigns(): List<AdvertisingCampaign> {
        return listOf(
            AdvertisingCampaign(
                id = "ad1",
                campaignName = "Gauteng VIP Header Banner",
                placement = "Homepage Banner",
                budgetZar = 1200,
                startDate = "2026-09-01",
                endDate = "2026-09-30",
                status = "ACTIVE",
                impressions = 28400,
                clicks = 1640
            ),
            AdvertisingCampaign(
                id = "ad2",
                campaignName = "Sandton Executive Move-to-Top",
                placement = "Category Top Sponsored",
                budgetZar = 450,
                startDate = "2026-09-10",
                endDate = "2026-09-17",
                status = "ACTIVE",
                impressions = 9200,
                clicks = 780
            )
        )
    }

    fun getInitialChatRooms(): List<ChatRoom> {
        return listOf(
            // --- 1. LOCAL HOOKUPS CATEGORY ---
            ChatRoom(
                id = "room_hookups_50km",
                name = "Local Hookups (50km Google Radar)",
                province = "Gauteng / National",
                city = "Near Me (50km)",
                description = "Find local casual hookups, instant adult dating meetups and nearby partners within a 50km distance radius.",
                activeUsersCount = 98,
                iconEmoji = "📍",
                isVipOnly = false,
                category = "Local Hookups"
            ),
            ChatRoom(
                id = "room_fwb_sa",
                name = "Friends with Benefits (FWB) SA",
                province = "National",
                city = "South Africa",
                description = "South African adult dating for those seeking friends with benefits, casual sex, or casual relationships.",
                activeUsersCount = 142,
                iconEmoji = "🔥",
                isVipOnly = false,
                category = "Local Hookups"
            ),
            ChatRoom(
                id = "room_jhb",
                name = "Johannesburg FWB & VIP Lounge",
                province = "Gauteng",
                city = "Johannesburg",
                description = "Local hookups, casual dating, nightlife & luxury rendezvous in Sandton, Rosebank & Fourways.",
                activeUsersCount = 84,
                iconEmoji = "🍸",
                isVipOnly = false,
                category = "Local Hookups"
            ),
            ChatRoom(
                id = "room_cpt",
                name = "Cape Town FWB & Casual Hookups",
                province = "Western Cape",
                city = "Cape Town",
                description = "Atlantic Seaboard chats, yacht parties, Camps Bay sunsets, local hookups and casual dating.",
                activeUsersCount = 68,
                iconEmoji = "🌊",
                isVipOnly = false,
                category = "Local Hookups"
            ),
            ChatRoom(
                id = "room_dbn",
                name = "Durban & Umhlanga Hookups & FWB",
                province = "KwaZulu-Natal",
                city = "Durban",
                description = "Umhlanga luxury, coastal vibes, sensual encounters and weekend hookups.",
                activeUsersCount = 42,
                iconEmoji = "🌴",
                isVipOnly = false,
                category = "Local Hookups"
            ),
            ChatRoom(
                id = "room_pta",
                name = "Pretoria & Centurion Casual FWB",
                province = "Gauteng",
                city = "Pretoria",
                description = "Menlyn, Centurion, Brooklyn and Hatfield discreet casual hookups and adult relaxation.",
                activeUsersCount = 38,
                iconEmoji = "💎",
                isVipOnly = false,
                category = "Local Hookups"
            ),

            // --- 2. LIFESTYLE EVENTS CATEGORY ---
            ChatRoom(
                id = "room_swingers_sa",
                name = "Swingers & Couples Club SA",
                province = "National",
                city = "South Africa",
                description = "Couples, throuples, and open-minded singles seeking swinger parties, lifestyle meetups, and discreet group fun.",
                activeUsersCount = 76,
                iconEmoji = "🥂",
                isVipOnly = false,
                category = "Lifestyle Events"
            ),
            ChatRoom(
                id = "room_spas",
                name = "Sensual Spas & Massage Lounge",
                province = "National",
                city = "South Africa",
                description = "Discussions, reviews and verified massage therapists across South Africa.",
                activeUsersCount = 31,
                iconEmoji = "💆‍♀️",
                isVipOnly = false,
                category = "Lifestyle Events"
            ),
            ChatRoom(
                id = "room_webcams",
                name = "Live Webcams Chat Room",
                province = "All South Africa",
                city = "Online Showroom",
                description = "Interactive live cam lounge, model stage requests, tipping tokens and private room shows.",
                activeUsersCount = 89,
                iconEmoji = "🎥",
                isVipOnly = false,
                isWebcamLounge = true,
                category = "Lifestyle Events"
            ),
            ChatRoom(
                id = "room_vip_events",
                name = "VIP Nightlife & Yacht Parties",
                province = "National",
                city = "Sandton & Cape Town",
                description = "Exclusive mansion soirees, rooftop cocktails, Clifton yacht cruises and private weekend gatherings.",
                activeUsersCount = 52,
                iconEmoji = "🍾",
                isVipOnly = true,
                category = "Lifestyle Events"
            ),

            // --- 3. GENERAL DISCUSSION CATEGORY ---
            ChatRoom(
                id = "room_general",
                name = "General South Africa Chat",
                province = "National",
                city = "All Cities",
                description = "Casual hangout, community introductions and general adult dating banter.",
                activeUsersCount = 64,
                iconEmoji = "🇿🇦",
                isVipOnly = false,
                category = "General Discussion"
            ),
            ChatRoom(
                id = "room_dating_tips",
                name = "Safe Dating & Verification Advice",
                province = "National",
                city = "South Africa",
                description = "Tips on meeting safely, verifying partner photos, discretion best practices and community guidelines.",
                activeUsersCount = 47,
                iconEmoji = "🛡️",
                isVipOnly = false,
                category = "General Discussion"
            ),
            ChatRoom(
                id = "room_relationship_talk",
                name = "Open Relationships & Chemistry Lounge",
                province = "National",
                city = "South Africa",
                description = "Discuss non-monogamy, boundary setting, chemistry, mutual pleasure and adult lifestyle advice.",
                activeUsersCount = 39,
                iconEmoji = "💬",
                isVipOnly = false,
                category = "General Discussion"
            )
        )
    }

    fun getInitialChatRoomMessages(roomId: String): List<ChatRoomMessage> {
        return when (roomId) {
            "room_fwb_sa" -> listOf(
                ChatRoomMessage("rf1", "room_fwb_sa", "u1", "Chantelle V.", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80", "Welcome to Friends with Benefits SA! 💖 Looking for ongoing casual dates and drama-free chemistry in Sandton.", "19:10", isFromMe = false, isModel = true, verifiedLevel = VerificationLevel.FULL),
                ChatRoomMessage("rf2", "room_fwb_sa", "u_guy_fwb1", "Alex_JHB", "", "Hey everyone! Anyone free for a spontaneous drink & casual meetup tonight in Rosebank?", "19:14", isFromMe = false, isModel = false),
                ChatRoomMessage("rf3", "room_fwb_sa", "u2", "Natasha K.", "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=500&auto=format&fit=crop&q=80", "Cape Town FWB vibes this weekend! Loving the warm weather ☀️ Check my profile for casual dinner dates.", "19:18", isFromMe = false, isModel = true, verifiedLevel = VerificationLevel.PHOTO),
                ChatRoomMessage("rf4", "room_fwb_sa", "u_guy_fwb2", "Riaan_CPT", "", "Love this room. FWB connections without the awkward expectations. 🥂", "19:22", isFromMe = false, isModel = false)
            )
            "room_hookups_50km" -> listOf(
                ChatRoomMessage("rh1", "room_hookups_50km", "u_radar_host", "Radar Bot", "", "📍 Google Maps 50km Radar active! 24 verified members currently within your 50km vicinity.", "19:00", isFromMe = false, isModel = false, verifiedLevel = VerificationLevel.FULL),
                ChatRoomMessage("rh2", "room_hookups_50km", "u4", "Chloe Daniels", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=500&auto=format&fit=crop&q=80", "Only 12km away in Fourways! Ready for a quick late evening hookup or wine chat 😘", "19:05", isFromMe = false, isModel = true, verifiedLevel = VerificationLevel.PHOTO),
                ChatRoomMessage("rh3", "room_hookups_50km", "u_guy3", "Shaun_Bryanston", "", "I'm in Bryanston, about 8km away. Hit me up if you want to grab drinks at Montecasino 🍸", "19:08", isFromMe = false, isModel = false)
            )
            "room_swingers_sa" -> listOf(
                ChatRoomMessage("rs1", "room_swingers_sa", "u_couple1", "Mark & Lisa (Couple)", "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?w=500&auto=format&fit=crop&q=80", "Hi all! Attractive couple (30/28) in Sandton hosting a private swinger cocktail evening this Saturday ✨", "18:30", isFromMe = false, isModel = false, verifiedLevel = VerificationLevel.FULL),
                ChatRoomMessage("rs2", "room_swingers_sa", "u_single1", "Gareth_VIP", "", "Sounds incredible! Sent you a private DM. Looking forward to connecting.", "18:35", isFromMe = false, isModel = false)
            )
            "room_jhb" -> listOf(
                ChatRoomMessage("rm1", "room_jhb", "u1", "Chantelle V.", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80", "Good evening everyone! I'll be in Sandton CBD tonight after 8 PM. Open for dinner bookings ✨", "18:42", isFromMe = false, isModel = true, verifiedLevel = VerificationLevel.FULL),
                ChatRoomMessage("rm2", "room_jhb", "u_guy1", "Marco_JHB", "", "Hey Chantelle! Just sent you a booking request for the weekend at The Leonardo 🥂", "18:45", isFromMe = false, isModel = false),
                ChatRoomMessage("rm3", "room_jhb", "u4", "Chloe Daniels", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=500&auto=format&fit=crop&q=80", "Rosebank tonight has amazing energy! Happy Wednesday everyone 💕", "18:48", isFromMe = false, isModel = true, verifiedLevel = VerificationLevel.PHOTO),
                ChatRoomMessage("rm4", "room_jhb", "u_guy2", "David_Sandton", "", "Any recommendations for high-end cocktail lounges open late in Melrose Arch?", "18:50", isFromMe = false, isModel = false)
            )
            "room_webcams" -> listOf(
                ChatRoomMessage("rmw1", "room_webcams", "mod_1", "Elena_Live", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=500&auto=format&fit=crop&q=80", "Welcome to the Live Webcams Show! Live streaming right now on Cam 1 💖", "Just now", isFromMe = false, isModel = true, verifiedLevel = VerificationLevel.FULL),
                ChatRoomMessage("rmw2", "room_webcams", "u_tip", "Johan_K", "", "Tipped 50 Tokens to Elena! ⭐ Awesome show!", "1m ago", isFromMe = false, isModel = false, tipAmountZar = 100),
                ChatRoomMessage("rmw3", "room_webcams", "mod_2", "Sasha_Cams", "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=500&auto=format&fit=crop&q=80", "Starting VIP private show in 5 minutes! Tap into room 3 😘", "2m ago", isFromMe = false, isModel = true, verifiedLevel = VerificationLevel.PHOTO)
            )
            "room_cpt" -> listOf(
                ChatRoomMessage("rmc1", "room_cpt", "u2", "Natasha K.", "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=500&auto=format&fit=crop&q=80", "Sunset champagne cruise booked for Saturday in Camps Bay 🌅 2 spots open!", "17:15", isFromMe = false, isModel = true, verifiedLevel = VerificationLevel.PHOTO),
                ChatRoomMessage("rmc2", "room_cpt", "u_cpt", "Gareth_CPT", "", "Cape Town weather is looking unbelievable this weekend. Enjoy guys!", "17:22", isFromMe = false, isModel = false)
            )
            else -> listOf(
                ChatRoomMessage("rmg1", roomId, "u_sys", "Pink Passions Host", "", "Welcome to the $roomId community lounge! Please respect verified members and enjoy discreet communication.", "12:00", isFromMe = false, isModel = false, verifiedLevel = VerificationLevel.FULL),
                ChatRoomMessage("rmg2", roomId, "u3", "Zara Rose", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=500&auto=format&fit=crop&q=80", "Hello all! Excited to meet verified gentlemen here. Feel free to say hi 🌸", "12:15", isFromMe = false, isModel = true, verifiedLevel = VerificationLevel.PHOTO)
            )
        }
    }

    fun getChatRoomUsers(roomId: String): List<ChatRoomUser> {
        return listOf(
            ChatRoomUser("u1", "Chantelle V.", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80", "Sandton", VerificationLevel.FULL, isVip = true, isModel = true),
            ChatRoomUser("u2", "Natasha K.", "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=500&auto=format&fit=crop&q=80", "Cape Town", VerificationLevel.PHOTO, isVip = true, isModel = true),
            ChatRoomUser("u3", "Zara Rose", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=500&auto=format&fit=crop&q=80", "Durban", VerificationLevel.PHOTO, isVip = false, isModel = true),
            ChatRoomUser("u4", "Chloe Daniels", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=500&auto=format&fit=crop&q=80", "Johannesburg", VerificationLevel.BASIC, isVip = false, isModel = true),
            ChatRoomUser("usr_g1", "Marco_JHB", "", "Johannesburg", VerificationLevel.BASIC, isVip = true, isModel = false),
            ChatRoomUser("usr_g2", "David_Sandton", "", "Sandton", VerificationLevel.NONE, isVip = false, isModel = false),
            ChatRoomUser("usr_g3", "Johan_K", "", "Pretoria", VerificationLevel.PHOTO, isVip = true, isModel = false)
        )
    }

    fun getInitialReviews(): List<Review> {
        return listOf(
            Review(
                id = "rev_1",
                reviewerId = "usr_cli_1",
                reviewerName = "Graham M. (Verified Client)",
                profileId = "p1",
                rating = 5,
                comment = "Chantelle is absolute perfection. Elegance, intelligence and warmth. We enjoyed an exceptional dinner at Marble Rosebank. 100% genuine and discreet.",
                createdAt = "2 days ago",
                moderationStatus = "APPROVED",
                responseText = "Thank you so much Graham! It was a delight dining with you. Looking forward to our next dinner date ✨"
            ),
            Review(
                id = "rev_2",
                reviewerId = "usr_cli_2",
                reviewerName = "Deon S.",
                profileId = "p1",
                rating = 5,
                comment = "Flawless experience from booking to goodbye. Truly a 5-star VIP companion in Sandton.",
                createdAt = "1 week ago",
                moderationStatus = "APPROVED"
            ),
            Review(
                id = "rev_3",
                reviewerId = "usr_cli_3",
                reviewerName = "Anton V.",
                profileId = "p2",
                rating = 5,
                comment = "Natasha made my Cape Town trip unforgettable. Fantastic conversation, stunning beauty and zero rush.",
                createdAt = "3 days ago",
                moderationStatus = "APPROVED"
            ),
            Review(
                id = "rev_4",
                reviewerId = "usr_cli_4",
                reviewerName = "Michael B.",
                profileId = "p3",
                rating = 5,
                comment = "Zara gave the most relaxing and thorough massage session in Durban. 10/10 professionalism.",
                createdAt = "5 days ago",
                moderationStatus = "APPROVED"
            )
        )
    }

    fun getInitialVerificationRequests(): List<VerificationRequest> {
        return listOf(
            VerificationRequest(
                id = "vr_1",
                userId = "u1",
                userName = "Chantelle V.",
                userEmail = "chantelle@pinkpassions.co.za",
                userMobile = "+27 82 123 4567",
                method = VerificationMethodType.FACIAL_BIOMETRICS,
                status = VerificationStatus.VERIFIED,
                documentType = "South African Smart ID Card",
                submittedAt = "Yesterday 14:30",
                matchScorePct = 98,
                livenessPassed = true,
                reviewerNotes = "Facial match confirmed against Smart ID and gallery photos. Approved Neon Green shield."
            ),
            VerificationRequest(
                id = "vr_2",
                userId = "u2",
                userName = "Natasha K.",
                userEmail = "natasha.k@gmail.com",
                userMobile = "+27 71 987 6543",
                method = VerificationMethodType.ID_PASSPORT,
                status = VerificationStatus.VERIFIED,
                documentType = "RSA Passport",
                submittedAt = "2 days ago",
                matchScorePct = 95,
                livenessPassed = true,
                reviewerNotes = "Passport copy validated. Watermark and EXIF stripped."
            ),
            VerificationRequest(
                id = "vr_3",
                userId = "u7",
                userName = "Jessica Leigh",
                userEmail = "jessica.l@yahoo.com",
                userMobile = "+27 83 456 7890",
                method = VerificationMethodType.FACIAL_BIOMETRICS,
                status = VerificationStatus.PENDING,
                documentType = "RSA Smart ID Card",
                submittedAt = "1 hour ago",
                matchScorePct = 94,
                livenessPassed = true,
                reviewerNotes = "Pending admin human audit in verification queue."
            )
        )
    }

    fun getInitialCommunityGroups(): List<CommunityGroup> {
        return listOf(
            CommunityGroup(
                id = "grp_jhb",
                name = "Gauteng Luxury Escorts & VIPs",
                description = "Private networking, client safety tips, venue recommendations in Sandton, Rosebank & Pretoria.",
                memberCount = 142,
                category = "Networking",
                isJoined = true,
                iconEmoji = "💎"
            ),
            CommunityGroup(
                id = "grp_cpt",
                name = "Cape Town Atlantic Seaboard Hub",
                description = "Camps Bay, Waterfront & Sea Point bookings, events, yacht parties and dinner rendezvous.",
                memberCount = 98,
                category = "Events",
                isJoined = false,
                iconEmoji = "🌊"
            ),
            CommunityGroup(
                id = "grp_safety",
                name = "Advertiser Safety & Verification Circle",
                description = "Real-time blacklists, client screening advice, security tips and legal compliance for South Africa.",
                memberCount = 310,
                category = "Safety",
                isJoined = true,
                iconEmoji = "🛡️"
            ),
            CommunityGroup(
                id = "grp_nightlife",
                name = "SA Nightlife, Clubs & Venues",
                description = "Club openings, VIP bottle service specials, dancer auditions and weekend nightlife guides.",
                memberCount = 215,
                category = "Nightlife",
                isJoined = false,
                iconEmoji = "🍸"
            )
        )
    }

    fun getInitialNotifications(): List<AppNotification> {
        return listOf(
            AppNotification(
                id = "notif_1",
                title = "Verified Member Badge Issued",
                message = "Congratulations! Your ID & Facial Verification passed. The Neon Green Shield is now active on your profile.",
                type = "VERIFICATION",
                timestamp = "10m ago"
            ),
            AppNotification(
                id = "notif_2",
                title = "Payment Successful (PayFast)",
                message = "Your VIP Platinum Subscription (R1,200) has been activated. Receipt INV-98241 generated.",
                type = "PAYMENT",
                timestamp = "1h ago"
            ),
            AppNotification(
                id = "notif_3",
                title = "New 5-Star Review Received",
                message = "Graham M. left a 5-star review: 'Chantelle is absolute perfection. Elegance, intelligence and warmth.'",
                type = "REVIEW",
                timestamp = "3h ago"
            ),
            AppNotification(
                id = "notif_4",
                title = "Featured Boost Live",
                message = "Your 7-Day Featured Spotlight is active. Your profile is currently pinned to top search in Sandton.",
                type = "PROMOTION",
                timestamp = "1d ago"
            )
        )
    }
}


